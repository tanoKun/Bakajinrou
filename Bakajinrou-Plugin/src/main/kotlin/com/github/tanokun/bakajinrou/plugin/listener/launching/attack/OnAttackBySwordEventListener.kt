package com.github.tanokun.bakajinrou.plugin.listener.launching.attack

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.method.item.SwordItem
import com.github.tanokun.bakajinrou.game.controller.AttackController
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.Plugin

class OnAttackBySwordEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
    attackController: AttackController,
): LifecycleEventListener(plugin, {
    register<EntityDamageByEntityEvent> { event ->
        val attacker = jinrouGame.getParticipant(event.damager.uniqueId) ?: return@register
        val victim = jinrouGame.getParticipant(event.entity.uniqueId) ?: return@register

        val attackerPlayer = event.damager as Player

        event.damage = 0.01

        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            event.isCancelled = true
            return@register
        }

        val attackMethod = attacker.getGrantedMethodByItemStack(attackerPlayer.inventory.itemInMainHand) ?: return@register

        if (attackMethod !is SwordItem) return@register

        attackController.attack(by = attacker, victim = victim, attackMethod)
    }
})