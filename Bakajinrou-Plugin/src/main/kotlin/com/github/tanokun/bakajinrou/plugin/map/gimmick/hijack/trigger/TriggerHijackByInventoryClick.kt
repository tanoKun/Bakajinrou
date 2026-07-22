package com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack.trigger

import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack.HijackFlow
import com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack.item.HijackItems
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.Plugin

class TriggerHijackByInventoryClick(
    plugin: Plugin,
    context: MapGimmickContext,
    items: HijackItems,
    flow: HijackFlow,
) : LifecycleEventListener(plugin, {
    register<InventoryClickEvent> { event ->
        val player = event.whoClicked as? Player ?: return@register
        if (event.clickedInventory !== player.inventory) return@register
        if (event.click != ClickType.LEFT) return@register

        val floor = items.floorsBySlot[event.slot] ?: return@register
        if (items.floorOf(event.currentItem) != floor) return@register

        val participant = context.participant(player) ?: return@register
        if (!participant.isAlive() || !isWolf(participant)) return@register

        flow.activate(player, floor)
    }
})
