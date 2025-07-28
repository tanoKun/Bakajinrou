package com.github.tanokun.bakajinrou.plugin.listener.launching.item

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.method.optional.OptionalMethod
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

class OptionalMethodEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
): LifecycleEventListener(plugin, {
    val drinking = hashMapOf<UUID, BukkitTask>()

    register<PlayerItemConsumeEvent> { event ->
        val consumer = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register

        val method = (consumer.getGrantedMethodByItemStack(event.item) as? OptionalMethod.DrinkingMethod) ?: return@register
        method.onConsume(consumer = consumer)
    }

    register<PotionSplashEvent> { event ->
        val potion = event.entity

        val shooterPlayer = (potion.shooter as? Player) ?: return@register
        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val method = (shooter.getGrantedMethodByItemStack(potion.item) as? OptionalMethod.ThrowMethod) ?: return@register

        method.onConsume(consumer = shooter)
    }

    register<PlayerInteractEvent> { event ->
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return@register
        if (event.hand != EquipmentSlot.HAND) return@register

        val item = event.item ?: return@register
        val consumer = jinrouGame.getParticipant(event.player.uniqueId) ?: return@register

        val method = (consumer.getGrantedMethodByItemStack(item) as? OptionalMethod.ClickMethod) ?: return@register
        method.onConsume(consumer = consumer)
    }
})