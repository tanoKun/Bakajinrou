package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.component.EachInfoBySurvivorComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.component.OfflineSurvivorComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.component.SharedInfoBySpectatorComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType
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
    private val game: JinrouGame,
    private val gameScheduler: GameScheduler,
    private val mainScope: CoroutineScope,
): Observer {
    init {
        mainScope.launch {
            gameScheduler
                .observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect { atStarted() }
        }
    }

    private fun atStarted() {
        tabHandler.createEngine(TabHandlerType.SharedBySpectators)

        game.getCurrentParticipants().forEach { participant ->
            mainScope.launch {
                val dummyUuid = dummyPlayers.getDummyUuidOrPut(participant.participantId.uniqueId)
                val player = playerProvider.waitPlayerOnline(participant)

                initializeForSpectator(player, dummyUuid, participant)
                initializeForSurvivor(player, participant, game.getCurrentParticipants())
            }
        }
    }

    private fun initializeForSpectator(player: Player, dummyUuid: DummyUUID, participant: Participant) {
        val type = TabHandlerType.SharedBySpectators

        if (participant.isPosition<SpectatorPosition>()) tabHandler.joinEngine(type, player)

        tabHandler.editEngine(type) {
            addComponent(SharedInfoBySpectatorComponent(dummyUuid, game, player, translator))
        }
    }

    private fun initializeForSurvivor(player: Player, survivor: Participant, participants: ParticipantScope.All) {
        if (survivor.isPosition<SpectatorPosition>()) return

        val type = TabHandlerType.EachPlayer(survivor.participantId)

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
}