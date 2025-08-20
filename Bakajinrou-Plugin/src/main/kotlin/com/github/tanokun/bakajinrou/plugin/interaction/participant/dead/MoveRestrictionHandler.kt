package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.listener.packet.LifecyclePacketListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.restriction.DisableHittingBody
import org.bukkit.GameMode
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class MoveRestrictionHandler(
    plugin: Plugin,
    disableHittingBody: DisableHittingBody,
    protocolManager: ProtocolManager
) : LifecyclePacketListener(plugin, protocolManager, {
    register(
        packet = arrayOf(
            PacketType.Play.Server.REL_ENTITY_MOVE,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
            PacketType.Play.Server.ENTITY_LOOK),
        listenerPriority = ListenerPriority.LOW
    ) { event, packet, receiver ->
        if (!disableHittingBody.ghostingPlayers.contains(packet.integers.read(0))) return@register
        if (receiver.gameMode != GameMode.SPECTATOR) event.isCancelled = true
    }
})