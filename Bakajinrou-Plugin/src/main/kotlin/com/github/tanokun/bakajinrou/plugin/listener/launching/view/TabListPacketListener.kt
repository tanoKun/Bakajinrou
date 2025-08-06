package com.github.tanokun.bakajinrou.plugin.listener.launching.view

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.wrappers.EnumWrappers
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.formatter.display.TabListModifier
import com.github.tanokun.bakajinrou.plugin.listener.packet.LifecyclePacketListener
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import net.kyori.adventure.translation.Translator
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.plugin.Plugin
import java.util.*

class TabListPacketListener(
    plugin: Plugin, jinrouGame: JinrouGame, protocolManager: ProtocolManager, translator: Translator
) : LifecyclePacketListener(plugin, protocolManager, {
    val tabModifier = TabListModifier(jinrouGame, translator)

    val suspendedPlayers = mutableSetOf<UUID>()

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!packet.playerInfoActions.read(0).contains(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE)) return@register

        val copy = packet.deepClone().apply {
            playerInfoDataLists.write(1, tabModifier.modifyByUpdateGameMode(receiver.uniqueId, packet.playerInfoDataLists.read(1)))
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
        if (jinrouGame.getParticipant(receiver.uniqueId) == null) return@register

        val players = packet.uuidLists.read(0).mapNotNull { jinrouGame.getParticipant(it) }
        suspendedPlayers.addAll(players.map(Participant::uniqueId))

        if (players.isNotEmpty()) event.isCancelled = true
    }

    onCancellation {
        val packet = ClientboundPlayerInfoRemovePacket(suspendedPlayers.filter { BukkitPlayerProvider.get(it) == null })

        jinrouGame.getAllParticipants().forEach {
            val player = (BukkitPlayerProvider.get(it) as? CraftPlayer) ?: return@forEach
            player.handle.connection.send(packet)
        }
    }
})