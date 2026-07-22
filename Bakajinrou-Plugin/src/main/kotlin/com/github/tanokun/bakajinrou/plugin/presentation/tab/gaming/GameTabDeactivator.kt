package com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.viewer.GameViewers
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandlerType
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GameTabDeactivator(
    private val playerProvider: BukkitPlayerProvider,
    private val tabHandler: TabHandler,
    private val game: GameStore,
    private val viewers: GameViewers,
    private val gameSession: JinrouGameSession,
    private val topScope: TopCoroutineScope,
): Observer {
    init {
        topScope.launch {
            gameSession
                .observeWin()
                .collect { tabDeactivate() }
        }
    }

    private fun tabDeactivate() {
        tabHandler.deleteEngine(TabHandlerType.SharedObserverView)

        game.getCurrentParticipants().forEach { participant ->
            tabHandler.deleteEngine(TabHandlerType.EachParticipant(participant.participantId))
        }

        viewers.current.forEach { viewer ->
            val player = playerProvider.getAllowNull(viewer.playerId) ?: return@forEach
            tabHandler.joinEngine(TabHandlerType.ShareInLobby, player)
        }
    }
}
