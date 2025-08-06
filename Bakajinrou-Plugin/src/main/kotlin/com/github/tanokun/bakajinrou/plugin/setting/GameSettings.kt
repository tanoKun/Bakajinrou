package com.github.tanokun.bakajinrou.plugin.setting

/*

import com.comphenix.protocol.ProtocolManager
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.plugin.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.finisher.CitizenSideFinisher
import com.github.tanokun.bakajinrou.plugin.listener.launching.PlayerConnectionEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackByBowEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackByPotionEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.attack.OnAttackBySwordEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.item.OnCraftEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.item.OptionalMethodEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.item.TransportMethodEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.view.PlayerChatEventListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.view.SecretItemPacketListener
import com.github.tanokun.bakajinrou.plugin.listener.launching.view.TabListPacketListener
import com.github.tanokun.bakajinrou.plugin.observer.ParticipantStateObserver
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.GameLifecycleUI
import com.github.tanokun.bakajinrou.plugin.setting.builder.game.*
import kotlinx.coroutines.launch
import net.minecraft.commands.execution.tasks.ContinuationTask.schedule
import net.minecraft.world.level.block.CaveVines.use
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.*
import kotlin.random.Random

class GameSettings(private val plugin: Plugin, private val protocolManager: ProtocolManager) {
    private val _candidates = hashSetOf<UUID>()

    private val _spectators = hashSetOf<UUID>()

    val candidates: Set<UUID>
        get() = _candidates

    val spectators: Set<UUID>
        get() = _spectators

    private val selectedPositions =
        hashMapOf(
            RequestedPositions.WOLF to 3,
            RequestedPositions.MADMAN to 1,
            RequestedPositions.IDIOT to 3,
            RequestedPositions.FORTUNE to 1,
            RequestedPositions.MEDIUM to 1,
            RequestedPositions.KNIGHT to 1,
            RequestedPositions.FOX to 1
        )

    var selectedMap: SelectedMap? = null

    fun getAmountBy(positions: RequestedPositions): Int {
        return selectedPositions[positions] ?: 0
    }


    fun updateAmount(positions: RequestedPositions, amount: Int) {
        if (amount < 0) throw IllegalArgumentException("予約役職を、0未満にはできません。")

        selectedPositions[positions] = amount
    }

    fun increase(positions: RequestedPositions) {
        updateAmount(positions, getAmountBy(positions) + 1)
    }

    fun decrease(positions: RequestedPositions) {
        updateAmount(positions, getAmountBy(positions) - 1)
    }

    fun addCandidate(uuid: UUID) {
        removeSpectator(uuid)

        _candidates.add(uuid)
    }

    fun addSpectator(uuid: UUID) {
        removeCandidate(uuid)

        _spectators.add(uuid)
    }

    fun removeCandidate(uuid: UUID) = _candidates.remove(uuid)

    fun removeSpectator(uuid: UUID) = _spectators.remove(uuid)

    fun buildGame(inject: DIContext.() -> Unit = {}): GameBuildResult {
        selectedMap?.let {
            if (selectedPositions.values.sum() > candidates.size) return GameBuildResult.IllegalSelectedPositions

            val (jinrou, controller) = GameBuilder(Random, plugin, Bukkit.getServer(), Bukkit.getScheduler(), protocolManager)
                .injection {
                    register(DebugLogger(plugin.logger))
                    register(BukkitPlayerProvider)
                    inject()
                }
                .setGameMap(it)
                .assignParticipants(selectedPositions, candidates) { builder ->
                    builder
                        .assignMadmans()
                        .assignWolfs(true)
                        .assignIdiots(IdiotAsFortunePosition, IdiotAsMediumPosition, IdiotAsKnightPosition)
                        .assignAbilityUsers()
                        .assignFox()
                        .assignOtherToCitizens()
                }
                .injection {

                }
                .assignSpectators(spectators)
                .createGameLogger()
                .createBodyHandler()
                .createGameScheduler()
                .createJinrouGame()
                .createItemFactory()
                .createControllers()
                .listeners {
                    use<OnAttackByBowEventListener>()
                    use<OnAttackBySwordEventListener>()
                    use<OnAttackByPotionEventListener>()

                    use<PlayerChatEventListener>()

                    //Item系統
                    use<OnCraftEventListener>()
                    use<OptionalMethodEventListener>()
                    use<SecretItemPacketListener>()
                    use<TransportMethodEventListener>()

                    use<TabListPacketListener>()

                    use<PlayerConnectionEventListener>()
                }
                .observers {
                    ParticipantStateObserver(get(), get(), plugin.minecraftDispatcher, plugin.asyncDispatcher)
                }
                .commonSchedules {
                    schedule<GameLifecycleUI> {
                        ::startingGame on Launching
                        ::finishGame on Cancellation
                    }

                    schedule<CitizenSideFinisher> {
                        ::onOverTime on OverTime
                    }
                }

            return GameBuildResult.SucceedCreation(jinrou, controller)
        }

        return GameBuildResult.NotFoundSettingMap
    }
}

fun onOverTime(game: JinrouGame, controller: JinrouGameController) {
    controller.mainDispatcherScope.launch { game.notifyWonCitizenFinish() }
}

sealed interface GameBuildResult {
    object NotFoundSettingMap: GameBuildResult
    object IllegalSelectedPositions: GameBuildResult
    class SucceedCreation(val jinrouGame: JinrouGame, val gameController: JinrouGameController): GameBuildResult

}*/
