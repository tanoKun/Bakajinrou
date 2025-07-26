package com.github.tanokun.bakajinrou.plugin.listener.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.formatter.display.PrefixModifier
import com.github.tanokun.bakajinrou.plugin.formatter.display.TabListModifier
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.plugin.Plugin

class TabListPacketListener(
    jinrouGame: JinrouGame, plugin: Plugin, protocolManager: ProtocolManager
) : LifecyclePacketListener(plugin, protocolManager, {
    val prefixModifiers = jinrouGame.participants.map { PrefixModifier(it) }

    val tabModifiers = jinrouGame.participants
        .associate { it.uniqueId to TabListModifier(viewer = it, prefixModifiers) }

    register(packet = PacketType.Play.Server.PLAYER_INFO) { packet, receiver ->
        val entries = packet.modifier.read(1) as MutableList<*>

        val modifier = tabModifiers[receiver.uniqueId] ?: return@register
        val modifiedList = modifier.modifyContents(entries.map { it as ClientboundPlayerInfoUpdatePacket.Entry })

        packet.modifier.write(1, modifiedList)
    }
})