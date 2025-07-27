package com.github.tanokun.bakajinrou.plugin.listener

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.method.effect.DamagePotionEffect
import com.github.tanokun.bakajinrou.api.attack.method.item.SwordItem
import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionType

class OnAttackEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
): LifecycleEventListener(plugin, {
    //ダイヤ剣
    register<EntityDamageByEntityEvent> { event ->
        val attacker = jinrouGame.getParticipant(event.damager.uniqueId) ?: return@register
        val victim = jinrouGame.getParticipant(event.entity.uniqueId) ?: return@register

        val attackerPlayer = event.damager as Player

        event.damage = 0.01

        val method = attacker.getGrantedMethodByItemStack(attackerPlayer.inventory.itemInMainHand) ?: return@register
        (method as? SwordItem)?.attack(by = attacker, victim = victim)
    }

    //弓 (矢)
    register<EntityDamageByEntityEvent> { event ->
        val attacker = (event.damager as? Arrow) ?: return@register

        val shooterPlayer = (attacker.shooter as? Player) ?: return@register
        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val method = (shooter.getGrantedMethodByItemStack(attacker.itemStack) as? ArrowMethod) ?: return@register

        val victim = jinrouGame.getParticipant(event.entity.uniqueId) ?: return@register

        event.damage = 0.01


        method.attack(by = shooter, victim = victim)
    }

    //ポーション (即時ダメージ)
    register<PotionSplashEvent> { event ->
        val potion = event.entity

        val shooterPlayer = (potion.shooter as? Player) ?: return@register
        val shooter = jinrouGame.getParticipant(shooterPlayer.uniqueId) ?: return@register
        val method = (shooter.getGrantedMethodByItemStack(potion.item) as? DamagePotionEffect) ?: return@register

        if (potion.potionMeta.basePotionType != PotionType.HARMING) return@register
        event.affectedEntities
            .filterIsInstance<Player>()
            .forEach { victimPlayer ->
                val victim = jinrouGame.getParticipant(victimPlayer.uniqueId) ?: return@forEach
                method.attack(by = shooter, victim = victim)
            }
    }
})