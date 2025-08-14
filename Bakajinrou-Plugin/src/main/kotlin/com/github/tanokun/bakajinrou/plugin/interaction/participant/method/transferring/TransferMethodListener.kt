package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.transferring

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.method.transferring.TransferMethod
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer.hasPossibilityOfMethod
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer.isTransportable
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class TransferMethodListener(
    plugin: Plugin,
    game: JinrouGame,
    transferMethod: TransferMethod,
    mainScope: CoroutineScope,
): LifecycleEventListener(plugin, {
    register<PlayerDropItemEvent> { event ->
        if (!game.existParticipant(event.player.uniqueId.asParticipantId())) return@register

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

        if (drop == pickup) return@register

        event.isCancelled = true
        event.item.remove()
        event.entity.playSound(Sound.sound(NamespacedKey("minecraft", "entity.item.pickup"), Sound.Source.PLAYER, 0.3f, 2f))

        mainScope.launch { transferMethod.transport(methodId, fromId = drop, toId = pickup) }
    }
})