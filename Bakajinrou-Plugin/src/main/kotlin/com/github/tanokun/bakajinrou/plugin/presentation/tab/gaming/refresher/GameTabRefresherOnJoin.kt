package com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.refresher

import com.github.tanokun.bakajinrou.api.player.asPlayerId
import com.github.tanokun.bakajinrou.game.viewer.GameViewer
import com.github.tanokun.bakajinrou.game.viewer.GameViewers
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandlerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class GameTabRefresherOnJoin(
    plugin: Plugin,
    mainScope: CoroutineScope,
    tabHandler: TabHandler,
    viewers: GameViewers
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent>(eventPriority = EventPriority.LOWEST) { event -> mainScope.launch {
        delay(100)

        val type = when (val viewer = viewers.find(event.player.uniqueId.asPlayerId())) {
            is GameViewer.Playing -> TabHandlerType.EachParticipant(viewer.participant.participantId)
            is GameViewer.DeadParticipant, is GameViewer.Spectator -> TabHandlerType.SharedObserverView
            null -> return@launch
        }

        tabHandler.joinEngine(type, event.player)
    } }
})
