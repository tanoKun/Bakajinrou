package com.github.tanokun.bakajinrou.plugin.participant.method.attack.synchronization

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.inventory.meta.Damageable

internal const val SCATTER_PROJECTILE_COUNT = 3

internal fun ItemStack.configureAsOneShotScatterCrossbow(): ItemStack = apply {
    editMeta { meta ->
        (meta as CrossbowMeta).apply {
            addEnchant(Enchantment.MULTISHOT, 1, true)
            setChargedProjectiles(
                List(SCATTER_PROJECTILE_COUNT) { ItemStack(Material.ARROW) }
            )
            setEnchantmentGlintOverride(true)
        }

        // Paper は拡散した矢ごとに耐久を1消費するため、3本目で壊れる値にする。
        (meta as Damageable).damage = type.maxDurability.toInt() - SCATTER_PROJECTILE_COUNT
    }
}
