package com.github.tanokun.bakajinrou.plugin.listener

import com.github.tanokun.bakajinrou.api.attack.AttackResult.*
import com.github.tanokun.bakajinrou.api.attack.AttackVerifier
import com.github.tanokun.bakajinrou.bukkit.controller.JinrouGameController
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class OnAttackListener(
    plugin: Plugin,
    gameController: JinrouGameController,
): LifecycleListener(plugin, {
    //ダイヤ剣
    register<EntityDamageByEntityEvent> { event ->
        val attacker = event.damager
        val victim = event.entity

        if (attacker !is Player) return@register
        if (victim !is Player) return@register

        val mainHandItem = attacker.inventory.itemInMainHand
        if (mainHandItem.type != Material.DIAMOND_SWORD) {
            event.damage = 0.01
            return@register
        }

        gameController.onAttack(by = AttackVerifier.Sword, to = victim.uniqueId) {
            when (it) {
                PROTECTED_BY_TOTEM -> { onProtectedByTotem(victim) }
                PROTECTED_BY_POTION_RESISTANCE -> { onProtectedByResistance(victim) }
                SUCCESS_ATTACK -> { onSuccess(attacker = attacker, victim = victim, controller = gameController) }
                INVALID_ATTACK -> event.isCancelled = true
                PROTECTED_BY_SHIELD -> return@onAttack
            }

            if (it != INVALID_ATTACK) removeMainHandOrOffHand(Material.DIAMOND_SWORD, attacker)
        }
    }

    //弓 (矢)
    register<EntityDamageByEntityEvent> { event ->
        val attacker = event.damager
        val victim = event.entity

        if (attacker !is Arrow) return@register
        if (victim !is Player) return@register

        val shooter = attacker.shooter
        if (shooter !is Player) return@register

        gameController.onAttack(by = AttackVerifier.Bow, to = victim.uniqueId) {
            when (it) {
                PROTECTED_BY_TOTEM -> { onProtectedByTotem(victim) }
                PROTECTED_BY_SHIELD -> { onProtectedByShield(victim) }
                PROTECTED_BY_POTION_RESISTANCE -> { onProtectedByResistance(victim) }
                SUCCESS_ATTACK -> { onSuccess(attacker = shooter, victim = victim, controller = gameController) }
                INVALID_ATTACK -> event.isCancelled = true
            }
        }
    }

    //ポーション (即時ダメージ)
    register<PotionSplashEvent> { event ->
        val attacker = event.entity

        println(attacker.potionMeta.basePotionType)

        if (attacker.potionMeta.basePotionType != PotionType.HARMING) return@register

        val shooter = attacker.shooter
        if (shooter !is Player) return@register

        event.affectedEntities
            .filterIsInstance<Player>()
            .forEach { victim ->
                gameController.onAttack(by = AttackVerifier.Potion, to = victim.uniqueId) {
                    when (it) {
                        PROTECTED_BY_TOTEM -> { onProtectedByTotem(victim) }
                        PROTECTED_BY_POTION_RESISTANCE -> { onProtectedByResistance(victim) }
                        SUCCESS_ATTACK -> { onSuccess(attacker = shooter, victim = victim, controller = gameController) }
                        INVALID_ATTACK -> event.isCancelled = true
                        PROTECTED_BY_SHIELD -> return@onAttack
                    }
                }
            }
    }
})

private fun onProtectedByTotem(victim: Player) {
    val world = victim.world

    world.playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.PLAYER, 1.0f, 1.0f))
    world.spawnParticle(Particle.TOTEM_OF_UNDYING, victim.location, 30, 0.5, 0.5, 0.5, 0.1)

    removeMainHandOrOffHand(Material.TOTEM_OF_UNDYING, victim)
}

private fun onProtectedByShield(victim: Player) {
    val world = victim.world

    world.playSound(Sound.sound(Key.key("item.shield.block"), Sound.Source.PLAYER, 1.0f, 1.0f))
    world.spawnParticle(Particle.BLOCK, victim.location, 10, 0.3, 0.3, 0.3, Material.OAK_WOOD.createBlockData())

    removeMainHandOrOffHand(Material.SHIELD, victim)
}

private fun onProtectedByResistance(victim: Player) {
    val world = victim.world

    world.playSound(Sound.sound(Key.key("entity.zombie_villager.cure"), Sound.Source.PLAYER, 1.0f, 1.0f))
    victim.removePotionEffect(PotionEffectType.RESISTANCE)
}


private fun onSuccess(attacker: Player, victim: Player, controller: JinrouGameController) {
    controller.killed(victim = victim.uniqueId, by = attacker.uniqueId)
}

private fun removeMainHandOrOffHand(material: Material, player: Player) {
    player.inventory.itemInMainHand.apply {
        if (type != material) return@apply

        player.inventory.setItemInMainHand(ItemStack.of(Material.AIR))
        return
    }

    player.inventory.itemInOffHand.apply {
        if (type != material) return@apply

        player.inventory.setItemInMainHand(ItemStack.of(Material.AIR))
    }
}