package com.github.tanokun.bakajinrou.plugin.common.bukkit.player

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.player.PlayerId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Single
import kotlin.coroutines.resume

@Single
class BukkitPlayerProvider(private val plugin: Plugin) {

    fun getAllowNull(participant: Participant) = getAllowNull(participant.participantId)

    fun getAllowNull(participantId: ParticipantId) = Bukkit.getPlayer(participantId.uniqueId)

    fun getAllowNull(playerId: PlayerId) = Bukkit.getPlayer(playerId.uniqueId)

    suspend fun waitPlayerOnline(participant: Participant) = waitPlayerOnline(participant.participantId)

    suspend fun waitPlayerOnline(playerId: PlayerId): Player = waitPlayerOnline(playerId.uniqueId)

    suspend fun waitPlayerOnline(participantId: ParticipantId): Player {
        return waitPlayerOnline(participantId.uniqueId)
    }

    private suspend fun waitPlayerOnline(uniqueId: java.util.UUID): Player {
        Bukkit.getPlayer(uniqueId)?.let { return it }

        return suspendCancellableCoroutine { continuation ->
            val listener = object : LifecycleEventListener(plugin, {
                register<PlayerJoinEvent> { event ->
                    if (event.player.uniqueId != uniqueId) return@register

                    continuation.resume(event.player)
                }
            }) {}

            listener.registerAll()

            continuation.invokeOnCancellation { listener.unregisterAll() }
        }
    }

    suspend fun waitPlayerOnline(participantId: ParticipantId, transaction: (Player) -> Unit) = transaction(waitPlayerOnline(participantId))
}
