package com.github.tanokun.bakajinrou.plugin.participant.method.attack.adapting

import com.github.tanokun.bakajinrou.api.attacking.method.ScatterCrossbowMethod
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.attacking.Attacking
import com.github.tanokun.bakajinrou.plugin.common.item.ItemPersistent.getMethodId
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.seconds

/**
 * 攻撃手段「拡散クロスボウ」の射出、命中、消費を Minecraft のイベントへ接続します。
 */
@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class AttackByScatterCrossbowAdapter(
    plugin: Plugin,
    attacking: Attacking,
    mainScope: CoroutineScope,
    scatterShots: ScatterShotTracker,
) : LifecycleEventListener(plugin, {
    register<EntityShootBowEvent> { event ->
        val shooter = event.entity as? Player ?: return@register
        val methodId = event.bow
            ?.takeIf { it.type == Material.CROSSBOW }
            ?.getMethodId()
            ?: return@register
        val projectile = event.projectile as? AbstractArrow ?: return@register

        projectile.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
        projectile.setMethodId(methodId)

        val isFirstProjectile = scatterShots.register(
            shooter.uniqueId.asParticipantId(),
            methodId,
            projectile.uniqueId,
        )
        if (!isFirstProjectile) return@register

        mainScope.launch {
            delay(SCATTER_SHOT_TIMEOUT)
            scatterShots.complete(methodId)?.let { expired ->
                attacking.consumeAttackMethod<ScatterCrossbowMethod>(
                    expired.shooterId,
                    expired.methodId,
                )
            }
        }
    }

    register<EntityDamageByEntityEvent> { event ->
        val projectile = event.damager as? AbstractArrow ?: return@register
        val methodId = projectile.getMethodId() ?: return@register

        event.isCancelled = true

        val attacker = projectile.shooter as? Player ?: return@register
        val victim = event.entity as? Player ?: return@register

        mainScope.launch {
            val resolutions = attacking.attack<ScatterCrossbowMethod>(
                by = attacker.uniqueId.asParticipantId(),
                victims = listOf(victim.uniqueId.asParticipantId()),
                withId = methodId,
            )
            if (resolutions.isNotEmpty()) {
                scatterShots.complete(methodId)
                return@launch
            }

            scatterShots.land(projectile.uniqueId)?.let { completed ->
                attacking.consumeAttackMethod<ScatterCrossbowMethod>(
                    completed.shooterId,
                    completed.methodId,
                )
            }
        }
    }

    register<ProjectileHitEvent> { event ->
        val projectile = event.entity as? AbstractArrow ?: return@register
        projectile.getMethodId() ?: return@register
        if (event.hitEntity is Player) return@register

        mainScope.launch {
            scatterShots.land(projectile.uniqueId)?.let { completed ->
                attacking.consumeAttackMethod<ScatterCrossbowMethod>(
                    completed.shooterId,
                    completed.methodId,
                )
            }
        }
    }
}) {
    private companion object {
        val SCATTER_SHOT_TIMEOUT = 30.seconds
    }
}
