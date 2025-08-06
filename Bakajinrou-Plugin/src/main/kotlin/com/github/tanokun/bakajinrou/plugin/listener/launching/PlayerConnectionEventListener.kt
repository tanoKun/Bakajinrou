/*
package com.github.tanokun.bakajinrou.plugin.listener.launching

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import com.github.tanokun.bakajinrou.plugin.formatter.display.updatePlayerListName
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class PlayerConnectionEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
    jinrouGameController: JinrouGameController,
    bodyHandler: BodyHandler
): LifecycleEventListener(plugin, {
    register<PlayerQuitEvent> { event ->
        val participant = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register

        event.quitMessage(null)

        participant.suspended()
        event.player.updatePlayerListName()

        jinrouGame.judge()?.let {
            jinrouGameController.finish(it)
        }
    }

    register<PlayerJoinEvent> { event ->
        val participant = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register

        event.joinMessage(null)

        participant.survived()
        bodyHandler.showBodies(participant.uniqueId)
        event.player.updatePlayerListName()
    }
})*/
