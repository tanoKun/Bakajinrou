package com.github.tanokun.bakajinrou.plugin.listener

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.formatter.display.updatePlayerListName
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class PlayerConnectionEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
): LifecycleEventListener(plugin, {
    register<PlayerQuitEvent> { event ->
        val participant = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register

        participant.suspended()
        event.player.updatePlayerListName()
    }

    register<PlayerJoinEvent> { event ->
        val participant = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register

        participant.survived()
        event.player.updatePlayerListName()
    }
})