package com.github.tanokun.bakajinrou.plugin.common.listener.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

abstract class LifecyclePacketListener internal constructor(
    private val plugin: Plugin, private val protocolManager: ProtocolManager, listenerDsl: LifecyclePacketListener.() -> Unit
): Listener, LifecycleListener {

    private val adapters = arrayListOf<PacketAdapter>()

    private val onCancellations = arrayListOf<() -> Unit>()

    init {
        listenerDsl(this)
    }

    internal fun register(
        packet: PacketType, listenerPriority: ListenerPriority = ListenerPriority.NORMAL, callback: (PacketEvent, PacketContainer, Player) -> Unit
    ) {
        val adapter = PacketAdapterObject(listenerPriority, packet, callback = callback)
        adapters.add(adapter)
    }


    internal fun register(
        vararg packet: PacketType, listenerPriority: ListenerPriority = ListenerPriority.NORMAL, callback: (PacketEvent, PacketContainer, Player) -> Unit
    ) {
        val adapter = PacketAdapterObject(listenerPriority, *packet, callback = callback)
        adapters.add(adapter)
    }

    internal fun onCancellation(canceller: () -> Unit) {
        onCancellations.add(canceller)
    }

    private inner class PacketAdapterObject(
        listenerPriority: ListenerPriority,
        vararg val packetTypes: PacketType,
        private val callback: (PacketEvent, PacketContainer, Player) -> Unit
    ) : PacketAdapter(plugin, listenerPriority, *packetTypes) {
        override fun onPacketSending(event: PacketEvent) {
            if (!packetTypes.contains(event.packetType)) return

            callback(event, event.packet, event.player)
        }

        override fun onPacketReceiving(event: PacketEvent) {
            if (!packetTypes.contains(event.packetType)) return

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

        onCancellations.forEach {
            it.invoke()
        }
    }
}