package com.github.tanokun.bakajinrou.plugin.interaction.method.hidden

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemViewer.isVisible
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.listener.packet.LifecyclePacketListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class HiddenItemListener(
    plugin: Plugin, game: JinrouGame, protocolManager: ProtocolManager
) : LifecyclePacketListener(plugin, protocolManager, {
    register(packet = PacketType.Play.Server.ENTITY_EQUIPMENT, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        val receiver = game.getParticipant(receiver.uniqueId.asParticipantId()) ?: return@register

        val items = packet.slotStackPairLists.read(0)

        val modifiedItems = HideItem.modifyEquipment(items, receiver)

        if (items == modifiedItems) return@register

        val packet = packet.deepClone().apply {
            slotStackPairLists.write(0, modifiedItems)
        }

        event.packet = packet
    }

    register(packet = PacketType.Play.Server.ANIMATION, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!game.existParticipant(receiver.uniqueId.asParticipantId())) return@register

        val animation = packet.integers.read(1)
        if (!HideItem.isAttackMotion(animation)) return@register

        val targetId = packet.integers.read(0)
        val targetPlayerNms = (receiver.world as CraftWorld).handle.entities.get(targetId) ?: return@register
        val targetPlayer = (targetPlayerNms.bukkitEntity as? Player) ?: return@register

        val item = targetPlayer.inventory.itemInMainHand

        if (item.isVisible()) return@register

        event.isCancelled = true
    }

    register(packet = PacketType.Play.Client.USE_ENTITY, listenerPriority = ListenerPriority.LOW) { event, packet, sender ->
        if (!game.existParticipant(sender.uniqueId.asParticipantId())) return@register

        val item = sender.inventory.itemInMainHand
        if (item.isVisible()) return@register

        val action = packet.enumEntityUseActions.read(0)
        if (HideItem.isAttack(action)) event.isCancelled = true
    }
})