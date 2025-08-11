package com.github.tanokun.bakajinrou.plugin.interaction.participant.state.dead.body

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.attack.BodyHandler
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import org.bukkit.Server

class BukkitBodyHandler(
    private val playerProvider: BukkitPlayerProvider,
    private val server: Server,
    private val game: JinrouGame
): BodyHandler {

    private val bodies = hashMapOf<ParticipantId, BodyPacket>()

    override fun createBody(of: ParticipantId) {
        val target = playerProvider.getAllowNull(of) ?: return
        val body = bodies.getOrPut(of) { BodyPacket(server, target) }

        game.getCurrentParticipants().forEach {
            val player = server.getPlayer(it.participantId.uniqueId) ?: return@forEach
            body.sendBody(to = player)
        }
    }

    override fun deleteBodies() {
        bodies.forEach { (_, body) ->
            game.getCurrentParticipants().forEach {
                val player = playerProvider.getAllowNull(it) ?: return@forEach
                body.remove(to = player)
            }
        }
    }

    override fun showBodies(to: ParticipantId) {
        val player = playerProvider.getAllowNull(to) ?: return

        bodies.forEach { (_, body) ->
            body.sendBody(to = player)
        }
    }
}