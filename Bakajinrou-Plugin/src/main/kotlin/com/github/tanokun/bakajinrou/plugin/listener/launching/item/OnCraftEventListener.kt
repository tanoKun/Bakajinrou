package com.github.tanokun.bakajinrou.plugin.listener.launching.item

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.method.BukkitItemFactory
import com.github.tanokun.bakajinrou.plugin.method.getGrantedMethodByItemStack
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import org.bukkit.Material
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

fun getMaxCraftAmount(inventory: CraftingInventory): Int {
    var result = Int.MAX_VALUE
    for (item in inventory.matrix) {
        if (item == null || item.type.isAir) continue
        result = minOf(result, item.amount)
    }
    return if (result == Int.MAX_VALUE) 0 else result
}

class OnCraftEventListener(
    plugin: Plugin,
    jinrouGame: JinrouGame,
    itemFactory: BukkitItemFactory,
): LifecycleEventListener(plugin, {
    register<CraftItemEvent> { event ->
        val crafter = jinrouGame.getParticipant(event.whoClicked.uniqueId) ?: return@register

        if (event.currentItem?.type != Material.QUARTZ_BLOCK) {
            event.currentItem?.let { event.isCancelled = crafter.getGrantedMethodByItemStack(it) == null }
            return@register
        }

        if (event.cursor.type != Material.AIR && !event.isShiftClick) return@register

        val repeat = if (event.isShiftClick) getMaxCraftAmount(inventory = event.inventory) else 1

        repeat(repeat) {
            val method = itemFactory.random()
            crafter.grantMethod(method)
            event.currentItem?.amount = 0

            if (event.isShiftClick) event.inventory.matrix.forEach { it?.amount -= 1 }

            if (!event.isShiftClick) {
                event.whoClicked.inventory
                    .filterNotNull()
                    .filter { it.persistentDataContainer.getOrDefault(itemKey, PersistentDataType.STRING, "") == method.uniqueId.toString() }
                    .forEach { it.amount = 0 }

                event.currentItem = method.createBukkitItem()
            }
        }
    }
})