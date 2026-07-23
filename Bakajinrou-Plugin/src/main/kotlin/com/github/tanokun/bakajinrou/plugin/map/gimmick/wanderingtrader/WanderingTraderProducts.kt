package com.github.tanokun.bakajinrou.plugin.map.gimmick.wanderingtrader

import com.github.tanokun.bakajinrou.game.crafting.CraftingProduct
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object WanderingTraderProducts {
    private val PRODUCT_KEY = NamespacedKey("jinrou", "wandering_trader_product")

    fun markProduct(product: CraftingProduct, item: ItemStack): ItemStack =
        item.apply {
            editMeta {
                it.persistentDataContainer.set(PRODUCT_KEY, PersistentDataType.STRING, product.name)
            }
        }

    fun productOf(item: ItemStack?): CraftingProduct? {
        val name = item?.persistentDataContainer?.get(PRODUCT_KEY, PersistentDataType.STRING)
            ?: return null
        return CraftingProduct.entries.find { it.name == name }
    }
}
