package com.github.tanokun.bakajinrou.plugin.participant.method.attack.adapting
import com.github.tanokun.bakajinrou.api.attacking.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attacking.method.ScatterCrossbowMethod
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.attacking.Attacking
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer.hasPossibilityOfMethod
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.seconds

/**
 * 攻撃手段「弓」が、攻撃に使用されることを検出します。
 *
 * @property plugin プラグイン
 * @property attacking 攻撃イベントの公開者
 * @property mainScope 監視用のコルーチンを起動するためのスコープ
 */
@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class AttackByBowAdapter(
    plugin: Plugin,
    attacking: Attacking,
    mainScope: CoroutineScope,
    scatterShots: ScatterShotTracker,
): LifecycleEventListener(plugin, {
    register<EntityDamageByEntityEvent> { event ->
        val projectile = event.damager as? AbstractArrow ?: return@register

        val scatterMethodId = projectile.getMethodId()
        if (scatterMethodId != null) {
            event.isCancelled = true

            val attacker = projectile.shooter as? Player ?: return@register
            val victim = event.entity as? Player ?: return@register

            mainScope.launch {
                val resolutions = attacking.attack<ScatterCrossbowMethod>(
                    by = attacker.uniqueId.asParticipantId(),
                    victims = listOf(victim.uniqueId.asParticipantId()),
                    withId = scatterMethodId,
                )
                if (resolutions.isNotEmpty()) {
                    scatterShots.complete(scatterMethodId)
                } else {
                    scatterShots.land(projectile.uniqueId)?.let { completed ->
                        attacking.consumeAttackMethod<ScatterCrossbowMethod>(
                            completed.shooterId,
                            completed.methodId,
                        )
                    }
                }
            }
            return@register
        }

        val arrow = projectile as? Arrow ?: return@register
        val attackMethod = arrow.itemStack.getMethodId() ?: return@register

        val attacker = arrow.shooter as? Player ?: return@register
        val victim = event.entity as? Player ?: return@register

        event.damage = 0.01

        mainScope.launch {
            attacking.attack<ArrowMethod>(by = attacker.uniqueId.asParticipantId(), victims = listOf(victim.uniqueId.asParticipantId()), attackMethod)
        }
    }

    register<ProjectileHitEvent> { event ->
        val projectile = event.entity as? AbstractArrow ?: return@register
        val scatterMethodId = projectile.getMethodId()
        if (scatterMethodId != null) {
            if (event.hitEntity is Player) return@register

            mainScope.launch {
                scatterShots.land(projectile.uniqueId)?.let { completed ->
                    attacking.consumeAttackMethod<ScatterCrossbowMethod>(
                        completed.shooterId,
                        completed.methodId,
                    )
                }
            }
            return@register
        }

        if (event.hitEntity != null) return@register
        val arrow = projectile as? Arrow ?: return@register
        val methodId = arrow.itemStack.getMethodId() ?: return@register

        val attacker = arrow.shooter as? Player ?: return@register
        val attackerId = attacker.uniqueId.asParticipantId()

        mainScope.launch {
            attacking.consumeArrow(attackerId, methodId)
        }
    }

    register<EntityShootBowEvent> { event ->
        val shooterPlayer = (event.entity as? Player) ?: return@register

        val bow = event.bow
        val scatterMethodId = bow?.takeIf { it.type == Material.CROSSBOW }?.getMethodId()
        if (scatterMethodId != null) {
            val projectile = event.projectile as? AbstractArrow ?: return@register
            projectile.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
            projectile.setMethodId(scatterMethodId)

            val isFirstProjectile = scatterShots.register(
                shooterPlayer.uniqueId.asParticipantId(),
                scatterMethodId,
                projectile.uniqueId,
            )
            if (isFirstProjectile) {
                mainScope.launch {
                    delay(SCATTER_SHOT_TIMEOUT)
                    scatterShots.complete(scatterMethodId)?.let { expired ->
                        attacking.consumeAttackMethod<ScatterCrossbowMethod>(
                            expired.shooterId,
                            expired.methodId,
                        )
                    }
                }
            }
            return@register
        }

        val arrow = event.consumable ?: return@register

        if (!arrow.hasPossibilityOfMethod()) return@register

        (event.projectile as? Arrow)?.let {
            it.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
        }

        mainScope.launch {
            attacking.shootArrow(shooterPlayer.uniqueId.asParticipantId())
        }
    }
}) {
    private companion object {
        val SCATTER_SHOT_TIMEOUT = 30.seconds
    }
}
