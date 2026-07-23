package com.github.tanokun.bakajinrou.plugin.map.gimmick.wanderingtrader

import com.github.tanokun.bakajinrou.game.crafting.CraftingProduct
import com.github.tanokun.bakajinrou.plugin.map.gimmick.GimmickMarkerTags
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.presentation.item.MethodItemPresenter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.WanderingTrader
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import java.util.Locale
import kotlin.random.Random

internal const val WANDERING_TRADER_ENTITY_TAG = "jinrou_gimmick_wandering_trader_entity"
internal const val WANDERING_TRADER_QUARTZ_COST = 5

class WanderingTraderFlow(
    private val context: MapGimmickContext,
    private val methodItemPresenter: MethodItemPresenter,
    private val random: Random,
) {
    private var trader: WanderingTrader? = null

    fun spawnTrader() {
        removeTrader()

        val location = context.markers(GimmickMarkerTags.WANDERING_TRADER)
            .randomOrNull(random)
            ?.location
            ?: return
        val world = location.world ?: return

        trader = world.spawn(location, WanderingTrader::class.java) { trader ->
            trader.customName(Component.text("行商人", NamedTextColor.GREEN))
            trader.isCustomNameVisible = true
            trader.isInvulnerable = true
            trader.isPersistent = true
            trader.setAI(true)
            trader.canPickupItems = false
            trader.despawnDelay = Int.MAX_VALUE
            trader.addScoreboardTag(WANDERING_TRADER_ENTITY_TAG)
            trader.recipes = createRecipes()
        }
    }

    fun removeTrader() {
        trader?.takeIf(WanderingTrader::isValid)?.remove()
        context.legacyLocation(0.0, 0.0, 0.0).world
            ?.getEntitiesByClass(WanderingTrader::class.java)
            ?.filter { WANDERING_TRADER_ENTITY_TAG in it.scoreboardTags }
            ?.forEach(WanderingTrader::remove)
        trader = null
    }

    private fun createRecipes(): List<MerchantRecipe> = listOf(
        createRecipe(CraftingProduct.SWORD, methodItemPresenter.sword(Locale.JAPAN)),
        createRecipe(CraftingProduct.GAS, methodItemPresenter.gas(Locale.JAPAN)),
        createRecipe(CraftingProduct.RESISTANCE, methodItemPresenter.resistance(Locale.JAPAN)),
        createRecipe(CraftingProduct.SHIELD, methodItemPresenter.shield(Locale.JAPAN)),
        createRecipe(CraftingProduct.SPEED, methodItemPresenter.speed(Locale.JAPAN)),
        createRecipe(CraftingProduct.INVISIBILITY, methodItemPresenter.invisibility(Locale.JAPAN)),
        createRecipe(CraftingProduct.EXCHANGE, methodItemPresenter.exchange(Locale.JAPAN)),
        createRecipe(CraftingProduct.SCATTER_CROSSBOW, methodItemPresenter.scatterCrossbow(Locale.JAPAN)),
    )

    private fun createRecipe(product: CraftingProduct, item: ItemStack): MerchantRecipe =
        MerchantRecipe(
            WanderingTraderProducts.markProduct(product, item),
            Int.MAX_VALUE,
        ).apply {
            addIngredient(ItemStack(Material.QUARTZ, WANDERING_TRADER_QUARTZ_COST))
            priceMultiplier = 0.0f
            demand = 0
            specialPrice = 0
        }
}
