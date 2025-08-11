package com.github.tanokun.bakajinrou.plugin.interaction.participant.state.update.listen

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.wrappers.EnumWrappers
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.listener.packet.LifecyclePacketListener
import com.github.tanokun.bakajinrou.plugin.interaction.participant.state.update.view.TabListModifier
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.plugin.Plugin

class TabListPacketListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
    protocolManager: ProtocolManager,
    tabModifier: TabListModifier,
    playerProvider: BukkitPlayerProvider
) : LifecyclePacketListener(plugin, protocolManager, {
    val suspendedPlayers = mutableSetOf<ParticipantId>()

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!packet.playerInfoActions.read(0).contains(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE)) return@register

        val copy = packet.deepClone().apply {
            playerInfoDataLists.write(1, tabModifier.modifyByUpdateGameMode(receiver.uniqueId.asParticipantId(), packet.playerInfoDataLists.read(1)))
        }

        event.packet = copy
    }

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!packet.playerInfoActions.read(0).contains(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME)) return@register

        val copy = packet.deepClone().apply {
            playerInfoDataLists.write(1, tabModifier.modifyByUpdateDisplayName(receiver, packet.playerInfoDataLists.read(1)))
        }

        event.packet = copy
    }

    register(packet = PacketType.Play.Server.PLAYER_INFO_REMOVE, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!jinrouGame.existParticipant(receiver.uniqueId.asParticipantId())) return@register

        val players = packet.uuidLists.read(0).mapNotNull { jinrouGame.getParticipant(it.asParticipantId()) }
        suspendedPlayers.addAll(players.map(Participant::participantId))

        if (players.isNotEmpty()) event.isCancelled = true
    }

    onCancellation {
        val packet = ClientboundPlayerInfoRemovePacket(
            suspendedPlayers
                .filter { playerProvider.getAllowNull(it) == null }
                .map { it.uniqueId }
        )

        jinrouGame.getCurrentParticipants().forEach {
            val player = (playerProvider.getAllowNull(it) as? CraftPlayer) ?: return@forEach
            player.handle.connection.send(packet)
        }
    }
})