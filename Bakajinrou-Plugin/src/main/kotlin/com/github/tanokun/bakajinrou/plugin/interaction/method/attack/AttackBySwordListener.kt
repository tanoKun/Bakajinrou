package com.github.tanokun.bakajinrou.plugin.interaction.method.attack

import com.github.tanokun.bakajinrou.api.attack.method.SwordMethod
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.attack.Attacking
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.Plugin

/**
 * 攻撃手段「剣」が、攻撃に使用されることを検出します。
 *
 * @property plugin プラグイン
 * @property attacking 攻撃イベントの公開者
 * @property mainScope 監視用のコルーチンを起動するためのスコープ
 */
class AttackBySwordListener(
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
