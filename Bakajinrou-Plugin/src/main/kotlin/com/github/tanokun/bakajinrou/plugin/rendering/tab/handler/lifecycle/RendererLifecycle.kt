package com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.lifecycle

import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class RendererLifecycle(
    private val tabHandler: TabHandler
): Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(e: PlayerJoinEvent) {
        tabHandler.createRenderer(e.player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onQuit(e: PlayerQuitEvent) {
        tabHandler.deleteRenderer(e.player)
    }
}