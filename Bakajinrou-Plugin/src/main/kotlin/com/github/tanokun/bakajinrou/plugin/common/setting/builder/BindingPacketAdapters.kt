package com.github.tanokun.bakajinrou.plugin.common.setting.builder

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

class BindingPacketAdapters(override val scope: Scope) : KoinScopeComponent, AutoCloseable {
    val listeners = scope.getAll<PacketAdapter>()

    override fun close() = listeners.forEach {
        ProtocolLibrary.getProtocolManager().removePacketListener(it)
    }

    fun registerAll() = listeners.forEach {
        ProtocolLibrary.getProtocolManager().addPacketListener(it)
    }

}