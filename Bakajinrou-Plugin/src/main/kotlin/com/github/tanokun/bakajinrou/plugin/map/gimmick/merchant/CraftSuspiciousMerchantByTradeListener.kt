package com.github.tanokun.bakajinrou.plugin.map.gimmick.merchant

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.MerchantInventory
import org.bukkit.plugin.Plugin
import kotlin.math.min

class CraftSuspiciousMerchantByTradeListener(
    plugin: Plugin,
    crafting: Crafting,
    mainScope: CoroutineScope,
) : LifecycleEventListener(plugin, {
    register<InventoryClickEvent> { event ->
        val inventory = event.inventory as? MerchantInventory ?: return@register
        val merchant = inventory.merchant as? Villager ?: return@register
        if (SUSPICIOUS_MERCHANT_ENTITY_TAG !in merchant.scoreboardTags) return@register
        if (event.rawSlot != RESULT_SLOT) return@register

        event.isCancelled = true

        if (event.click !in supportedClicks) return@register
        if (!event.isShiftClick && !event.cursor.type.isAir) return@register
        if (event.currentItem?.type != Material.QUARTZ_BLOCK) return@register

        val player = event.whoClicked as? Player ?: return@register
        val availableCrafts = inventory.quartzAmount() / QUARTZ_COST
        val repeat = if (event.isShiftClick) availableCrafts else min(availableCrafts, 1)
        if (repeat <= 0) return@register

        inventory.consumeQuartz(repeat * QUARTZ_COST)
        val style = if (event.isShiftClick) CraftingStyle.BULK else CraftingStyle.SINGLE

        repeat(repeat) {
            mainScope.launch {
                crafting.randomlyCraftMethod(player.uniqueId.asParticipantId(), style)
            }
        }

        player.playSound(player.location, Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f)
    }
}) {
    private companion object {
        const val RESULT_SLOT = 2
        const val QUARTZ_COST = 3

        val supportedClicks = setOf(
            ClickType.LEFT,
            ClickType.RIGHT,
            ClickType.SHIFT_LEFT,
            ClickType.SHIFT_RIGHT,
        )

        fun MerchantInventory.quartzAmount(): Int = (0..1)
            .mapNotNull(::getItem)
            .filter { it.type == Material.QUARTZ }
            .sumOf { it.amount }

        fun MerchantInventory.consumeQuartz(amount: Int) {
            var remaining = amount

            for (slot in 0..1) {
                val item = getItem(slot) ?: continue
                if (item.type != Material.QUARTZ) continue

                val consumed = min(item.amount, remaining)
                item.amount -= consumed
                remaining -= consumed

                if (item.amount <= 0) setItem(slot, null)
                if (remaining == 0) return
            }
        }
    }
}
