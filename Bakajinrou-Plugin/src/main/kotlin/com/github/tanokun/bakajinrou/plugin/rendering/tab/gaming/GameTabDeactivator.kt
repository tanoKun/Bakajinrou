package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GameTabDeactivator(
    private val playerProvider: BukkitPlayerProvider,
    private val tabHandler: TabHandler,
    private val game: JinrouGame,
    private val gameSession: JinrouGameSession,
    private val topScope: TopCoroutineScope,
): Observer {
    init {
        topScope.launch {
            gameSession
                .observeWin(topScope)
                .collect { tabDeactivate() }
        }
    }

    private fun tabDeactivate() {
        tabHandler.deleteEngine(TabHandlerType.SharedBySpectators)

        game.getCurrentParticipants().forEach { participant ->
            if (!participant.isPosition<SpectatorPosition>())
                tabHandler.deleteEngine(TabHandlerType.EachPlayer(participant.participantId))

            val player = playerProvider.getAllowNull(participant.participantId) ?: return@forEach
            tabHandler.joinEngine(TabHandlerType.ShareInLobby, player)
        }
    }
}