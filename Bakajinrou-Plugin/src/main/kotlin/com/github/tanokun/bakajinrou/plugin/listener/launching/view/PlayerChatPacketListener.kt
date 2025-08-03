package com.github.tanokun.bakajinrou.plugin.listener.launching.view

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.chat.ChatIntegrity
import com.github.tanokun.bakajinrou.plugin.listener.packet.LifecyclePacketListener
import org.bukkit.plugin.Plugin

class PlayerChatPacketListener(
    plugin: Plugin, jinrouGame: JinrouGame, protocolManager: ProtocolManager, chatIntegrity: ChatIntegrity = ChatIntegrity()
) : LifecyclePacketListener(plugin, protocolManager, {
    register(
        packet = PacketType.Play.Server.CHAT,
        listenerPriority = ListenerPriority.LOW
    ) { event, packet, receiver ->
        val sender = jinrouGame.getParticipant(packet.uuiDs.read(0)) ?: return@register
        val receiver = jinrouGame.getParticipant(receiver.uniqueId) ?: return@register

        event.isCancelled = chatIntegrity.verify(sender, receiver)
    }
})