package com.github.tanokun.bakajinrou.plugin.interaction.method.transfer.listen

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.method.transfer.TransferMethod
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemViewer.hasPossibilityOfMethod
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemViewer.isTransportable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.plugin.Plugin

class TransferMethodListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
    transferMethod: TransferMethod,
    mainScope: CoroutineScope,
): LifecycleEventListener(plugin, {
    register<PlayerDropItemEvent> { event ->
        if (!jinrouGame.existParticipant(event.player.uniqueId.asParticipantId())) return@register

        val item = event.itemDrop.itemStack

        if (!item.hasPossibilityOfMethod()) return@register

        if (!item.isTransportable()) {
            event.isCancelled = true
            return@register
        }
    }

    register<PlayerDropItemEvent> { event ->
        val item = event.itemDrop.itemStack

        if (item.type == Material.BOW) event.isCancelled = true
    }

    register<EntityPickupItemEvent> { event ->
        val methodId = event.item.itemStack.getMethodId() ?: return@register

        val drop = event.item.thrower?.asParticipantId() ?: return@register
        val pickup = event.entity.uniqueId.asParticipantId()

        event.isCancelled = true
        event.item.remove()
        event.entity.playSound(Sound.sound(NamespacedKey("minecraft", "entity.item.pickup"), Sound.Source.PLAYER, 0.3f, 2f))

        mainScope.launch { transferMethod.transport(methodId, fromId = drop, toId = pickup) }
    }
})