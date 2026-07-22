package com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.audience.AudienceChange
import com.github.tanokun.bakajinrou.game.audience.GameAudience
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.presentation.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.component.EachInfoBySurvivorComponent
import com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.component.OfflineSurvivorComponent
import com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.component.SharedInfoByObserverComponent
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandlerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GameTabInitializer(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val tabHandler: TabHandler,
    private val dummyPlayers: DummyPlayers,
    private val game: GameStore,
    private val audience: GameAudience,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
): Observer {
    private var started = false

    init {
        mainScope.launch {
            gameScheduler
                .observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect { atStarted() }
        }

        mainScope.launch {
            audience.changes.collect(::onAudienceChanged)
        }
    }

    private fun atStarted() {
        tabHandler.createEngine(TabHandlerType.SharedObserverView)
        started = true

        game.getCurrentParticipants().forEach { participant ->
            mainScope.launch {
                val dummyUuid = dummyPlayers.getDummyUuidOrPut(participant.participantId.uniqueId)
                val player = playerProvider.waitPlayerOnline(participant)

                initializeForObserver(dummyUuid, player)
                initializeForParticipant(player, participant, game.getCurrentParticipants())
            }
        }

        audience.current.spectators.forEach { playerId ->
            mainScope.launch {
                tabHandler.joinEngine(TabHandlerType.SharedObserverView, playerProvider.waitPlayerOnline(playerId))
            }
        }
    }

    private fun initializeForObserver(dummyUuid: DummyUUID, target: Player) {
        tabHandler.editEngine(TabHandlerType.SharedObserverView) {
            addComponent(SharedInfoByObserverComponent(dummyUuid, game, target, translator))
        }
    }

    private fun initializeForParticipant(player: Player, participant: Participant, participants: ParticipantScope.All) {
        val type = TabHandlerType.EachParticipant(participant.participantId)

        tabHandler.createEngine(type)

        tabHandler.joinEngine(type, player)
        tabHandler.editEngine(type) {
            participants
                .map { playerProvider.getAllowNull(it) to it.participantId.uniqueId }
                .forEach { (player, uuid) ->
                    val dummyUuid = dummyPlayers.getDummyUuidOrPut(uuid)
                    if (player == null) {
                        addComponent(OfflineSurvivorComponent(dummyUuid, PlayerNameCache.get(uuid) ?: "Unknown", uuid))
                        return@editEngine
                    }

                    addComponent(EachInfoBySurvivorComponent(dummyUuid, game, player, translator))
                }
        }
    }

    private suspend fun onAudienceChanged(change: AudienceChange) {
        if (!started) return

        when (change) {
            is AudienceChange.Joined -> {
                val player = playerProvider.waitPlayerOnline(change.playerId)
                tabHandler.joinEngine(TabHandlerType.SharedObserverView, player)
            }
            is AudienceChange.Left -> {
                val player = playerProvider.getAllowNull(change.playerId) ?: return
                tabHandler.joinEngine(TabHandlerType.ShareInLobby, player)
            }
        }
    }
}
