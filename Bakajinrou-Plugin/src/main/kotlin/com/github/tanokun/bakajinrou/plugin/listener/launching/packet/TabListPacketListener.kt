package com.github.tanokun.bakajinrou.plugin.listener.launching.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.wrappers.EnumWrappers
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.formatter.display.PrefixModifier
import com.github.tanokun.bakajinrou.plugin.formatter.display.TabListModifier
import com.github.tanokun.bakajinrou.plugin.listener.packet.LifecyclePacketListener
import org.bukkit.plugin.Plugin

class TabListPacketListener(
    plugin: Plugin, jinrouGame: JinrouGame, protocolManager: ProtocolManager
) : LifecyclePacketListener(plugin, protocolManager, {
    val prefixModifiers = jinrouGame.participants.map { PrefixModifier(it) }

    val tabModifiers = jinrouGame.participants
        .associate { it.uniqueId to TabListModifier(viewer = it, prefixModifiers) }

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!packet.playerInfoActions.read(0).contains(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE)) return@register
        val tabModifier = tabModifiers[receiver.uniqueId] ?: return@register

        val copy = packet.deepClone().apply {
            playerInfoDataLists.write(1, tabModifier.modifyByUpdateGameMode(packet.playerInfoDataLists.read(1)))
        }

        event.packet = copy
    }

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!packet.playerInfoActions.read(0).contains(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME)) return@register
        val tabModifier = tabModifiers[receiver.uniqueId] ?: return@register

        val copy = packet.deepClone().apply {
            playerInfoDataLists.write(1, tabModifier.modifyByUpdateDisplayName(packet.playerInfoDataLists.read(1)))
        }

        event.packet = copy
    }

    register(packet = PacketType.Play.Server.PLAYER_INFO_REMOVE, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        val players = packet.uuidLists.read(0).mapNotNull { jinrouGame.getParticipant(it) }

        if (players.isNotEmpty()) event.isCancelled = true
    }
})