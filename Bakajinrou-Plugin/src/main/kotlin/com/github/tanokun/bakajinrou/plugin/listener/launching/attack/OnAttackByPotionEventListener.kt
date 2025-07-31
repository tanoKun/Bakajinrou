package com.github.tanokun.bakajinrou.plugin.listener.launching.attack

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.method.effect.DamagePotionEffect
import com.github.tanokun.bakajinrou.api.participant.nonSpectators
import com.github.tanokun.bakajinrou.game.controller.AttackController
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
    attackController: AttackController,
): LifecycleEventListener(plugin, {
    register<PotionSplashEvent> { event ->
        val potion = event.entity

        val shooterPlayer = (potion.shooter as? Player) ?: return@register
        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val attackMethod = (shooter.getGrantedMethodByItemStack(potion.item) as? DamagePotionEffect) ?: return@register

        if (potion.potionMeta.basePotionType != PotionType.HARMING) return@register

        val victims = event.affectedEntities
            .filterIsInstance<Player>()
            .mapNotNull { jinrouGame.getParticipant(it.uniqueId) }

        attackController.attack(by = shooter, victims = victims.nonSpectators(), attackMethod)
    }

    register<ProjectileLaunchEvent> { event ->
        val potion = event.entity as? ThrownPotion ?: return@register
        val shooterPlayer = (potion.shooter as? Player) ?: return@register

        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val damagePotionEffectMethod = shooter.getGrantedMethodByItemStack(potion.item) ?: return@register

        if (damagePotionEffectMethod !is DamagePotionEffect) return@register

        attackController.throwPotion(shooter, damagePotionEffectMethod)
    }
})