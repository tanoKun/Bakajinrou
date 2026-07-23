package com.github.tanokun.bakajinrou.plugin.map.gimmick.botanical

import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.CraftingProduct
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemViewer.translateBasic
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.Locale

object BotanicalMerchantProducts {
    private val PRODUCT_KEY = NamespacedKey("jinrou", "botanical_product")

    fun createPreview(product: CraftingProduct, translator: JinrouTranslator): ItemStack =
        ItemStack(material(product)).apply {
            editMeta { meta ->
                meta.addItemFlags(*ItemFlag.entries.toTypedArray())
                meta.persistentDataContainer.set(
                    PRODUCT_KEY,
                    PersistentDataType.STRING,
                    product.name,
                )
            }
            translateBasic(assetKey(product), translator, Locale.JAPAN)
        }

    fun productOf(item: ItemStack?): CraftingProduct? {
        val name = item?.persistentDataContainer?.get(PRODUCT_KEY, PersistentDataType.STRING)
            ?: return null
        return CraftingProduct.entries.find { it.name == name }
    }

    private fun material(product: CraftingProduct): Material = when (product) {
        CraftingProduct.SWORD -> Material.DIAMOND_SWORD
        CraftingProduct.GAS -> Material.SPLASH_POTION
        CraftingProduct.RESISTANCE -> Material.POTION
        CraftingProduct.SHIELD -> Material.SHIELD
        CraftingProduct.SPEED -> Material.SPLASH_POTION
        CraftingProduct.INVISIBILITY -> Material.POTION
        CraftingProduct.EXCHANGE -> Material.ENDER_PEARL
        CraftingProduct.SCATTER_CROSSBOW -> Material.CROSSBOW
    }

    private fun assetKey(product: CraftingProduct): MethodAssetKeys = when (product) {
        CraftingProduct.SWORD -> MethodAssetKeys.Attack.SWORD
        CraftingProduct.GAS -> MethodAssetKeys.Attack.GAS
        CraftingProduct.RESISTANCE -> MethodAssetKeys.Protective.RESISTANCE
        CraftingProduct.SHIELD -> MethodAssetKeys.Protective.SHIELD
        CraftingProduct.SPEED -> MethodAssetKeys.Advantage.SPEED
        CraftingProduct.INVISIBILITY -> MethodAssetKeys.Advantage.INVISIBILITY
        CraftingProduct.EXCHANGE -> MethodAssetKeys.Advantage.EXCHANGE
        CraftingProduct.SCATTER_CROSSBOW -> MethodAssetKeys.Attack.SCATTER_CROSSBOW
    }
}
