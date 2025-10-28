package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Server
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped
@Scope(value = GameComponents::class)
class BukkitBodyHandler(
    private val playerProvider: BukkitPlayerProvider,
    private val server: Server,
    private val game: JinrouGame,
    private val mainScope: CoroutineScope
) {

    private val bodies = hashMapOf<ParticipantId, BodyPacket>()

    fun createBody(of: ParticipantId) {
        val target = playerProvider.getAllowNull(of) ?: return
        val body = bodies.getOrPut(of) { BodyPacket(server, target, mainScope) }

        game.getCurrentParticipants().forEach {
            val player = server.getPlayer(it.participantId.uniqueId) ?: return@forEach
            body.sendBody(to = player)
        }
    }

    fun deleteBodies() {
        bodies.forEach { (_, body) ->
            game.getCurrentParticipants().forEach {
                val player = playerProvider.getAllowNull(it) ?: return@forEach
                body.remove(to = player)
            }
        }
    }

    fun showBodies(to: ParticipantId) {
        val player = playerProvider.getAllowNull(to) ?: return

        bodies.forEach { (_, body) ->
            body.sendBody(to = player)
        }
    }
}