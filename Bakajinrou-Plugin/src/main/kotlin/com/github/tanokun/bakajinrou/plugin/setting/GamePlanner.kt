package com.github.tanokun.bakajinrou.plugin.setting

import com.comphenix.protocol.ProtocolManager
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.StrategyIntegrity
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.game.controller.AttackController
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.game.logger.GameLogger
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.schedule.TimeSchedule
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onCancellationByOvertime
import com.github.tanokun.bakajinrou.plugin.finisher.CitizenSideFinisher
import com.github.tanokun.bakajinrou.plugin.method.BukkitItemFactory
import com.github.tanokun.bakajinrou.plugin.observer.ParticipantStateObserver
import com.github.tanokun.bakajinrou.plugin.setting.factory.GameListenerRegistry
import com.github.tanokun.bakajinrou.plugin.setting.factory.PositionAssigner
import com.github.tanokun.bakajinrou.plugin.setting.factory.SelectedMap
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

typealias JinrouGameProvider = (List<Participant>) -> JinrouGame
typealias LoggerProvider = () -> GameLogger
typealias GameSchedulerProvider = (Long, List<TimeSchedule>, Plugin) -> GameScheduler
typealias BodyHandlerProvider = () -> BodyHandler

class GamePlanner(
    private val jinrouGameProvider: JinrouGameProvider,
    private val loggerProvider: LoggerProvider,
    private val gameSchedulerProvider: GameSchedulerProvider,
    private val bodyHandlerProvider: BodyHandlerProvider,
    private val positionAssigner: PositionAssigner
) {
    var selectedMap: SelectedMap? = null

    private val candidates: MutableSet<Player> = mutableSetOf()

    private val spectators: MutableSet<Player> = mutableSetOf()

    /**
     * 新しい人狼ゲームを構築し、ゲーム本体とそのコントローラーを生成します。
     *
     * @param plugin Bukkitプラグインインスタンス
     * @param playerNameCache プレイヤー名のキャッシュ
     * @param protocolManager ProtocolLibのプロトコルマネージャ。パケット操作に使用
     *
     * @return 作成された JinrouGame と JinrouGameController のペア。
     *
     * @throws IllegalStateException マップ未選択、参加人数不足時
     */
    fun createGame(plugin: Plugin, playerNameCache: PlayerNameCache, protocolManager: ProtocolManager): Pair<JinrouGame, JinrouGameController> {
        val selectedMap = selectedMap ?: throw IllegalStateException("マップが選択されていません。")

        if (positionAssigner.getMinimumRequired() > candidates.size) throw IllegalStateException("現在の参加人数では、選択されている役職が多すぎます。")

        val strategyIntegrity = StrategyIntegrity()

        val gameLogger: GameLogger = loggerProvider()
        val schedules = selectedMap.createSchedules()
        val scheduler: GameScheduler = gameSchedulerProvider(selectedMap.startTime, schedules, plugin)
        val bodyHandler: BodyHandler = bodyHandlerProvider()
        val jinrouGame: JinrouGame = jinrouGameProvider(positionAssigner.assignPositions(candidates, spectators, strategyIntegrity))

        val jinrouGameController = JinrouGameController(jinrouGame, scheduler, plugin.logger, plugin.minecraftDispatcher)
        val attackController = AttackController(gameLogger, bodyHandler, DebugLogger(plugin.logger, playerNameCache), jinrouGame, jinrouGameController)

        val itemFactory = BukkitItemFactory(plugin)

        ParticipantStateObserver(jinrouGame, jinrouGameController, plugin.asyncDispatcher, plugin.minecraftDispatcher)
        addDefinitiveSchedules(scheduler, plugin, jinrouGame, jinrouGameController, attackController, itemFactory, protocolManager)

        return jinrouGame to jinrouGameController
    }

    private fun addDefinitiveSchedules(
        scheduler: GameScheduler,
        plugin: Plugin,
        jinrouGame: JinrouGame,
        jinrouGameController: JinrouGameController,
        attackController: AttackController,
        itemFactory: BukkitItemFactory,
        protocolManager: ProtocolManager
    ) {
        GameListenerRegistry(plugin, jinrouGame, attackController, itemFactory, protocolManager).apply {
            scheduler.addSchedule(asRegisterSchedule())
            scheduler.addSchedule(asUnRegisterSchedule())
        }

        scheduler.addSchedule(onCancellationByOvertime {
            jinrouGameController.finish(CitizenSideFinisher(jinrouGame))
        })
    }
}