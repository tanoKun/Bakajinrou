package com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.refresher

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.DummyPlayers
import com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.component.EachInfoBySurvivorComponent
import com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.component.SharedInfoByObserverComponent
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandlerType

abstract class GameTabRefresher(
    private val game: GameStore,
    private val tabHandler: TabHandler,
    private val dummyPlayers: DummyPlayers,
    private val playerProvider: BukkitPlayerProvider,
    private val jinrouTranslator: JinrouTranslator,
): Observer {
    protected fun rerender(target: Participant) {
        val dummyUuid = dummyPlayers.getDummyUuidOrPut(target.participantId.uniqueId)
        val player = playerProvider.getAllowNull(target) ?: return

        game.getCurrentParticipants()
            .forEach {
                tabHandler.editEngine(TabHandlerType.EachParticipant(it.participantId)) {
                    updateComponent(EachInfoBySurvivorComponent(dummyUuid, game, player, jinrouTranslator))
                }
            }

        tabHandler.editEngine(TabHandlerType.SharedObserverView) {
            updateComponent(SharedInfoByObserverComponent(dummyUuid, game, player, jinrouTranslator))
        }
    }
}
