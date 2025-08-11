package com.github.tanokun.bakajinrou.plugin.interaction.method.attack.listen

import com.github.tanokun.bakajinrou.api.attack.method.SwordMethod
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.attack.Attacking
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemPersistent.getMethodId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.Plugin

class OnAttackBySwordEventListener(
    plugin: Plugin, attacking: Attacking, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    register<EntityDamageByEntityEvent> { event ->
        val attacker = event.damager as? Player ?: return@register
        val victim = event.entity as? Player ?: return@register

        val attackerPlayer = event.damager as Player

        event.damage = 0.01

        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            event.isCancelled = true
            return@register
        }

        val attackMethod = attackerPlayer.inventory.itemInMainHand.getMethodId() ?: return@register

        mainScope.launch {
            attacking.attack<SwordMethod>(by = attacker.uniqueId.asParticipantId(), victims = listOf(victim.uniqueId.asParticipantId()), attackMethod)
        }
    }
})
