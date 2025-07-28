package com.github.tanokun.bakajinrou.plugin.listener.launching.attack

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.method.appearance.BowItem
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.plugin.Plugin

class OnAttackByBowEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
): LifecycleEventListener(plugin, {
    register<EntityDamageByEntityEvent> { event ->
        val attacker = (event.damager as? Arrow) ?: return@register

        val shooterPlayer = (attacker.shooter as? Player) ?: return@register
        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val attackMethod = (shooter.getGrantedMethodByItemStack(attacker.itemStack) as? ArrowMethod) ?: return@register

        val victim = jinrouGame.getParticipant(event.entity.uniqueId) ?: return@register

        event.damage = 0.01

        attackMethod.attack(by = shooter, victim = victim)
        shooter.removeMethod(attackMethod)
    }

    register<EntityShootBowEvent> { event ->
        val shooterPlayer = (event.entity as? Player) ?: return@register
        val bow = event.bow ?: return@register
        val arrow = event.consumable ?: return@register

        (event.projectile as? Arrow)?.let {
            it.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
        }

        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val arrowMethod = shooter.getGrantedMethodByItemStack(arrow) ?: return@register

        if (shooter.getGrantedMethodByItemStack(bow) !is BowItem || arrowMethod !is ArrowMethod) {
            event.isCancelled = true
            return@register
        }

        arrowMethod.onConsume(shooter)
    }
})