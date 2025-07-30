package com.github.tanokun.bakajinrou.plugin.listener.launching.item

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.wrappers.Pair
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.plugin.listener.packet.LifecyclePacketListener
import com.github.tanokun.bakajinrou.plugin.method.isVisibleKey
import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import org.bukkit.Material
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class SecretItemPacketListener(
    plugin: Plugin, jinrouGame: JinrouGame, protocolManager: ProtocolManager
) : LifecyclePacketListener(plugin, protocolManager, {
    register(packet = PacketType.Play.Server.ENTITY_EQUIPMENT, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        val receiver = jinrouGame.getParticipant(receiver.uniqueId) ?: return@register

        val items = packet.slotStackPairLists.read(0)

        val modifiedItems = items.mapNotNull { pair ->
            val item = pair.second

            if (item.persistentDataContainer.getOrDefault(isVisibleKey, PersistentDataType.BOOLEAN, true)) return@mapNotNull pair
            if (receiver.isPosition<SpectatorPosition>() || receiver.state == ParticipantStates.DEAD) return@mapNotNull pair

            return@mapNotNull Pair(pair.first, ItemStack(Material.AIR))
        }

        if (items == modifiedItems) return@register

        val packet = packet.deepClone().apply {
            slotStackPairLists.write(0, modifiedItems)
        }

        event.packet = packet
    }

    register(packet = PacketType.Play.Server.ANIMATION, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (jinrouGame.getParticipant(receiver.uniqueId) == null) return@register

        val animation = packet.integers.read(1)
        if (animation != ClientboundAnimatePacket.SWING_OFF_HAND && animation != ClientboundAnimatePacket.SWING_MAIN_HAND) return@register

        val targetId = packet.integers.read(0)
        val targetPlayerNms = (receiver.world as CraftWorld).handle.entities.get(targetId) ?: return@register
        val targetPlayer = (targetPlayerNms.bukkitEntity as? Player) ?: return@register

        val item = targetPlayer.inventory.itemInMainHand

        if (item.persistentDataContainer.getOrDefault(isVisibleKey, PersistentDataType.BOOLEAN, true)) return@register

        event.isCancelled = true
    }
})