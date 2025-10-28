package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming.refresher

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType
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
    game: JinrouGame
): LifecycleEventListener(plugin, {
    register<PlayerJoinEvent>(eventPriority = EventPriority.LOWEST) { event -> mainScope.launch {
        delay(100)

        val participantId = event.player.uniqueId.asParticipantId()

        val participant = game.getParticipant(participantId) ?: return@launch

        val type = if (participant.isDead())
            TabHandlerType.SharedBySpectators
        else
            TabHandlerType.EachPlayer(event.player.uniqueId.asParticipantId())

        tabHandler.joinEngine(type, event.player)
    } }
})
