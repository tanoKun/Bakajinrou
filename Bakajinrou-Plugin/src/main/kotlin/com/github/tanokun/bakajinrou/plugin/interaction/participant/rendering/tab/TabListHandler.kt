package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.wrappers.EnumWrappers
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.listener.packet.LifecyclePacketListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class TabListHandler(
    plugin: Plugin,
    game: JinrouGame,
    protocolManager: ProtocolManager,
    playerProvider: BukkitPlayerProvider
) : LifecyclePacketListener(plugin, protocolManager, {
    val suspendedPlayers = mutableSetOf<ParticipantId>()

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        val actions = packet.playerInfoActions.read(0)
        if (!actions.contains(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE)) return@register
        val receiverParticipant = game.getParticipant(receiver.uniqueId.asParticipantId()) ?: return@register

        if (receiverParticipant.isVisibleSpectators()) return@register

        event.packet = event.packet.deepClone().apply {
            this.playerInfoActions.write(0, actions - EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE)
        }
    }

    register(packet = PacketType.Play.Server.PLAYER_INFO_REMOVE, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!game.existParticipant(receiver.uniqueId.asParticipantId())) return@register

        val players = packet.uuidLists.read(0).mapNotNull { game.getParticipant(it.asParticipantId()) }
        suspendedPlayers.addAll(players.map(Participant::participantId))

        if (players.isNotEmpty()) event.isCancelled = true
    }

    onCancellation {
        val packet = ClientboundPlayerInfoRemovePacket(
            suspendedPlayers
                .filter { playerProvider.getAllowNull(it) == null }
                .map { it.uniqueId }
        )

        game.getCurrentParticipants().forEach {
            val player = (playerProvider.getAllowNull(it) as? CraftPlayer) ?: return@forEach
            player.handle.connection.send(packet)
        }
    }
})