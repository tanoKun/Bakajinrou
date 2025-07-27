package com.github.tanokun.bakajinrou.plugin.listener.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleListener
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

abstract class LifecyclePacketListener internal constructor(
    private val plugin: Plugin, private val protocolManager: ProtocolManager, listenerDsl: LifecyclePacketListener.() -> Unit
): Listener, LifecycleListener {

    private val adapters = arrayListOf<PacketAdapter>()

    init {
        listenerDsl(this)
    }

    internal fun register(
        packet: PacketType, listenerPriority: ListenerPriority = ListenerPriority.NORMAL, callback: (PacketEvent, PacketContainer, Player) -> Unit
    ) {
        val adapter = PacketAdapterObject(listenerPriority, packet, callback)
        adapters.add(adapter)
    }

    private inner class PacketAdapterObject(
        listenerPriority: ListenerPriority, private val packetType: PacketType, private val callback: (PacketEvent, PacketContainer, Player) -> Unit
    ) : PacketAdapter(plugin, listenerPriority, packetType) {
        override fun onPacketSending(event: PacketEvent) {
            if (event.packetType != packetType) return

            callback(event, event.packet, event.player)
        }

        override fun onPacketReceiving(event: PacketEvent) {
            if (event.packetType != packetType) return

            callback(event, event.packet, event.player)
        }
    }

    override fun registerAll() {
        adapters.forEach { adapter ->
            protocolManager.addPacketListener(adapter)
        }
    }

    override fun unregisterAll() {
        adapters.forEach { adapter ->
            protocolManager.removePacketListener(adapter)
        }
    }
}