package com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack.trigger

import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack.item.HijackItems
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.plugin.Plugin

class CancelInvalidHijackOperationListener(
    plugin: Plugin,
    items: HijackItems,
) : LifecycleEventListener(plugin, {
    register<InventoryClickEvent> { event ->
        val player = event.whoClicked as? Player ?: return@register
        if (event.clickedInventory === player.inventory && event.slot in items.floorsBySlot) {
            event.isCancelled = true
            return@register
        }

        if (event.action == InventoryAction.COLLECT_TO_CURSOR) {
            val fixedItems = items.floorsBySlot.keys.map(player.inventory::getItem)
            if (fixedItems.any { it?.isSimilar(event.cursor) == true }) event.isCancelled = true
        }
    }

    register<InventoryDragEvent> { event ->
        val touchesFixedSlot = event.rawSlots.any { rawSlot ->
            event.view.getInventory(rawSlot) === event.whoClicked.inventory &&
                event.view.convertSlot(rawSlot) in items.floorsBySlot
        }

        if (touchesFixedSlot) event.isCancelled = true
    }
})
