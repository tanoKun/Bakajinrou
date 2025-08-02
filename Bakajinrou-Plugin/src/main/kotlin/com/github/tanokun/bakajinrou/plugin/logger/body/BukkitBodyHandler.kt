package com.github.tanokun.bakajinrou.plugin.logger.body

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import org.bukkit.Server
import java.util.*

class BukkitBodyHandler(
    private val server: Server,
    private val participants: ParticipantScope.All
): BodyHandler {

    private val bodies = hashMapOf<UUID, BodyPacket>()

    override fun createBody(of: UUID) {
        val target = server.getPlayer(of) ?: throw IllegalArgumentException("オフラインのプレイヤーを追加することはできません。")
        val body = bodies.getOrPut(of) { BodyPacket(server, target) }

        participants.forEach {
            val player = server.getPlayer(it.uniqueId) ?: return@forEach
            body.sendBody(to = player)
        }
    }

    override fun deleteBodies() {
        bodies.forEach { (_, body) ->
            participants.forEach {
                val player = server.getPlayer(it.uniqueId) ?: return@forEach
                body.remove(to = player)
            }
        }
    }

    override fun showBodies(to: UUID) {
        val player = server.getPlayer(to) ?: return

        bodies.forEach { (_, body) ->
            body.sendBody(to = player)
        }
    }
}