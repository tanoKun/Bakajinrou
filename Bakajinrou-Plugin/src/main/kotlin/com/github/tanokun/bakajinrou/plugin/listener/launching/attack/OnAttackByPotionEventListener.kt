package com.github.tanokun.bakajinrou.plugin.listener.launching.attack

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.method.effect.DamagePotionEffect
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionType

class OnAttackByPotionEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
): LifecycleEventListener(plugin, {
    register<PotionSplashEvent> { event ->
        val potion = event.entity

        val shooterPlayer = (potion.shooter as? Player) ?: return@register
        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val attackMethod = (shooter.getGrantedMethodByItemStack(potion.item) as? DamagePotionEffect) ?: return@register

        if (potion.potionMeta.basePotionType != PotionType.HARMING) return@register
        event.affectedEntities
            .filterIsInstance<Player>()
            .forEach { victimPlayer ->
                val victim = jinrouGame.getParticipant(victimPlayer.uniqueId) ?: return@forEach

                attackMethod.attack(by = shooter, victim = victim)
            }

        shooter.removeMethod(attackMethod)
    }

    register<ProjectileLaunchEvent> { event ->
        val potion = event.entity as? ThrownPotion ?: return@register
        val shooterPlayer = (potion.shooter as? Player) ?: return@register

        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val damagePotionEffectMethod = shooter.getGrantedMethodByItemStack(potion.item) ?: return@register

        if (damagePotionEffectMethod !is DamagePotionEffect) {
            event.isCancelled = true
            return@register
        }

        damagePotionEffectMethod.onConsume(shooter)
    }
})