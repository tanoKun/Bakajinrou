package com.github.tanokun.bakajinrou.plugin.interaction.method.attack

import com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.attack.Attacking
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.item.ItemViewer.hasPossibilityOfMethod
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.plugin.Plugin

/**
 * 攻撃手段「弓」が、攻撃に使用されることを検出します。
 *
 * @property plugin プラグイン
 * @property attacking 攻撃イベントの公開者
 * @property mainScope 監視用のコルーチンを起動するためのスコープ
 */
class AttackByBowListener(
    plugin: Plugin, attacking: Attacking, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    register<EntityDamageByEntityEvent> { event ->
        val arrow = (event.damager as? Arrow) ?: return@register

        val attacker = arrow.shooter as? Player ?: return@register
        val victim = event.entity as? Player ?: return@register

        event.damage = 0.01

        val attackMethod = arrow.itemStack.getMethodId() ?: return@register

        mainScope.launch {
            attacking.attack<ArrowMethod>(by = attacker.uniqueId.asParticipantId(), victims = listOf(victim.uniqueId.asParticipantId()), attackMethod)
        }
    }

    register<EntityShootBowEvent> { event ->
        val shooterPlayer = (event.entity as? Player) ?: return@register
        val arrow = event.consumable ?: return@register

        if (!arrow.hasPossibilityOfMethod()) return@register

        (event.projectile as? Arrow)?.let {
            it.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
        }

        mainScope.launch {
            attacking.arrowShoot(shooterPlayer.uniqueId.asParticipantId())
        }
    }
})
