package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.refresher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.DummyPlayers
import com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.component.EachInfoBySurvivorComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.component.SharedInfoBySpectatorComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType

abstract class GameTabRefresher(
    private val game: JinrouGame,
    private val tabHandler: TabHandler,
    private val dummyPlayers: DummyPlayers,
    private val playerProvider: BukkitPlayerProvider,
    private val jinrouTranslator: JinrouTranslator,
): Observer {
    protected fun rerender(target: Participant) {
        val dummyUuid = dummyPlayers.getDummyUuidOrPut(target.participantId.uniqueId)
        val player = playerProvider.getAllowNull(target) ?: return

        game.getCurrentParticipants()
            .excludeSpectators()
            .forEach {
                tabHandler.editEngine(TabHandlerType.EachPlayer(it.participantId)) {
                    updateComponent(EachInfoBySurvivorComponent(dummyUuid, game, player, jinrouTranslator))
                }
            }

        tabHandler.editEngine(TabHandlerType.SharedBySpectators) {
            updateComponent(SharedInfoBySpectatorComponent(dummyUuid, game, player, jinrouTranslator))
        }
    }
}