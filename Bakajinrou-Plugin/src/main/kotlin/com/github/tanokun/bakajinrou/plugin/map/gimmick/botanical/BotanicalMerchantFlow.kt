package com.github.tanokun.bakajinrou.plugin.map.gimmick.botanical

import com.github.tanokun.bakajinrou.game.crafting.CraftingProduct
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.WanderingTrader
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.random.Random

internal const val BOTANICAL_MERCHANT_ENTITY_TAG = "jinrou_gimmick_botanical_trader"
internal const val BOTANICAL_MERCHANT_QUARTZ_COST = 5

class BotanicalMerchantFlow(
    private val context: MapGimmickContext,
    private val translator: JinrouTranslator,
    private val random: Random,
) {
    private var merchant: WanderingTrader? = null

    fun spawnMerchant() {
        removeMerchant()

        val location = context.markers(GimmickMarkerTags.BOTANICAL_MERCHANT)
            .randomOrNull(random)
            ?.location
            ?: return
        val world = location.world ?: return

        merchant = world.spawn(location, WanderingTrader::class.java) { trader ->
            trader.customName(Component.text("植物園の行商人", NamedTextColor.GREEN))
            trader.isCustomNameVisible = true
            trader.isInvulnerable = true
            trader.isPersistent = true
            trader.setAI(true)
            trader.canPickupItems = false
            trader.despawnDelay = Int.MAX_VALUE
            trader.addScoreboardTag(BOTANICAL_MERCHANT_ENTITY_TAG)
            trader.recipes = CraftingProduct.entries.map(::createRecipe)
        }
    }

    fun removeMerchant() {
        merchant?.takeIf(WanderingTrader::isValid)?.remove()
        context.legacyLocation(0.0, 0.0, 0.0).world
            ?.getEntitiesByClass(WanderingTrader::class.java)
            ?.filter { BOTANICAL_MERCHANT_ENTITY_TAG in it.scoreboardTags }
            ?.forEach(WanderingTrader::remove)
        merchant = null
    }

    private fun createRecipe(product: CraftingProduct): MerchantRecipe =
        MerchantRecipe(
            BotanicalMerchantProducts.createPreview(product, translator),
            Int.MAX_VALUE,
        ).apply {
            addIngredient(ItemStack(Material.QUARTZ, BOTANICAL_MERCHANT_QUARTZ_COST))
            priceMultiplier = 0.0f
            demand = 0
            specialPrice = 0
        }
}
