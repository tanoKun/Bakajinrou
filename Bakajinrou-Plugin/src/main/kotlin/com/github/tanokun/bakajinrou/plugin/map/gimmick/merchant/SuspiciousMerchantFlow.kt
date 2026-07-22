package com.github.tanokun.bakajinrou.plugin.map.gimmick.merchant

import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

internal const val SUSPICIOUS_MERCHANT_ENTITY_TAG = "jinrou_gimmick_suspicious_merchant"

class SuspiciousMerchantFlow(
    private val context: MapGimmickContext,
) {
    fun spawnMerchant() {
        val legacy = context.legacyLocation(-343.0, 34.0, -413.0)
        val location = context.markerLocation(GimmickMarkerTags.MERCHANT, legacy)
        val world = location.world ?: return

        world.spawn(location, Villager::class.java) { villager ->
            villager.customName(Component.text("怪しい村人", NamedTextColor.DARK_PURPLE))
            villager.isCustomNameVisible = true
            villager.isSilent = true
            villager.setAI(false)
            villager.profession = Villager.Profession.FARMER
            villager.villagerLevel = 5
            villager.getAttribute(Attribute.MAX_HEALTH)?.baseValue = 2.0
            villager.health = 2.0
            villager.addScoreboardTag(SUSPICIOUS_MERCHANT_ENTITY_TAG)
            villager.recipes = listOf(
                MerchantRecipe(ItemStack(Material.QUARTZ_BLOCK), Int.MAX_VALUE).apply {
                    addIngredient(ItemStack(Material.QUARTZ, QUARTZ_COST))
                }
            )
        }

        context.broadcast(
            Component.text("マップ中央に怪しい村人が現れた……。", NamedTextColor.DARK_PURPLE)
        )
    }

    fun removeMerchant() {
        context.legacyLocation(0.0, 0.0, 0.0).world
            ?.getEntitiesByClass(Villager::class.java)
            ?.filter { SUSPICIOUS_MERCHANT_ENTITY_TAG in it.scoreboardTags }
            ?.forEach(Villager::remove)
    }

    private companion object {
        const val QUARTZ_COST = 3
    }
}
