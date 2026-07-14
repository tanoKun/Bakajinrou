package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import java.util.UUID

@Scoped
@Scope(value = GameComponents::class)
class BukkitBodyHandler(
    private val playerProvider: BukkitPlayerProvider,
    private val server: Server,
    private val game: JinrouGame,
    private val mainScope: CoroutineScope
) {

    private val bodies = hashMapOf<ParticipantId, BodyPacket>()

    private val visibleBodyByViewer = hashMapOf<UUID, BodyPacket>()

    private val shownBodiesByViewer = hashMapOf<UUID, MutableSet<BodyPacket>>()

    fun createBody(of: ParticipantId) {
        val target = playerProvider.getAllowNull(of) ?: return
        val body = bodies.getOrPut(of) { BodyPacket(server, target, mainScope) }

        game.getCurrentParticipants().forEach {
            val player = server.getPlayer(it.participantId.uniqueId) ?: return@forEach
            if (!body.isInWorld(player)) return@forEach

            body.sendBody(to = player)
            shownBodiesByViewer.getOrPut(player.uniqueId, ::hashSetOf).add(body)
            refreshNameVisibility(player)
        }
    }

    fun deleteBodies() {
        shownBodiesByViewer.forEach { (viewerId, shownBodies) ->
            val player = server.getPlayer(viewerId) ?: return@forEach
            shownBodies.forEach { body ->
                body.remove(to = player)
            }
        }

        bodies.clear()
        visibleBodyByViewer.clear()
        shownBodiesByViewer.clear()
    }

    fun showBodies(to: ParticipantId) {
        val player = playerProvider.getAllowNull(to) ?: return

        visibleBodyByViewer.remove(player.uniqueId)

        shownBodiesByViewer.remove(player.uniqueId)?.forEach { body ->
            body.remove(player)
        }

        val shownBodies = bodies.values.filter { it.isInWorld(player) }
        shownBodies.forEach { body ->
            body.sendBody(to = player)
        }

        if (shownBodies.isNotEmpty()) {
            shownBodiesByViewer[player.uniqueId] = shownBodies.toHashSet()
        }

        refreshNameVisibility(player)
    }

    fun refreshNameVisibility(viewer: Player, eyeLocation: Location = viewer.eyeLocation) {
        val direction = eyeLocation.direction
        val obstructionDistance = viewer.world.rayTraceBlocks(
            eyeLocation,
            direction,
            MAX_NAME_DISTANCE,
            FluidCollisionMode.NEVER,
            true
        )?.hitPosition?.distance(eyeLocation.toVector())

        val target = shownBodiesByViewer[viewer.uniqueId].orEmpty()
            .mapNotNull { body ->
                body.intersectionDistance(eyeLocation, MAX_NAME_DISTANCE, obstructionDistance)
                    ?.let { distance -> body to distance }
            }
            .minByOrNull { (_, distance) -> distance }
            ?.first

        val previous = visibleBodyByViewer[viewer.uniqueId]
        if (previous === target) return

        previous?.hideName(viewer)

        if (target == null) {
            visibleBodyByViewer.remove(viewer.uniqueId)
            return
        }

        target.showName(viewer)
        visibleBodyByViewer[viewer.uniqueId] = target
    }

    private companion object {
        const val MAX_NAME_DISTANCE = 3.0
    }
}
