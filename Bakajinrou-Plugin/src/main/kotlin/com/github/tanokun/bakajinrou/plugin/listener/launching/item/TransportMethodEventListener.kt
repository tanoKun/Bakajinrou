package com.github.tanokun.bakajinrou.plugin.listener.launching.item

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.Plugin

class TransportMethodEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
): LifecycleEventListener(plugin, {
    val dropKey = "droppingItem"

    register<PlayerDropItemEvent> { event ->
        val dropper = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register
        val method = dropper.getGrantedMethodByItemStack(event.itemDrop.itemStack) ?: return@register

        if (method !is AsBukkitItem) return@register

        if (!method.transportable) {
            event.isCancelled = true
            return@register
        }

        dropper.removeMethod(method)
        event.itemDrop.setMetadata(dropKey, FixedMetadataValue(plugin, method))
    }

    register<PlayerAttemptPickupItemEvent> { event ->
        val pickupPlayer = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register
        val method = event.item.getMetadata(dropKey).getOrNull(0)?.value() ?: return@register

        if (method !is AsBukkitItem) return@register

        event.isCancelled = true

        pickupPlayer.grantMethod(method)
        event.item.remove()
    }
})