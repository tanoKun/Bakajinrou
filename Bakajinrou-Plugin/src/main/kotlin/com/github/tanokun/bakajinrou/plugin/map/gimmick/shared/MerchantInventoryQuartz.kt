package com.github.tanokun.bakajinrou.plugin.map.gimmick.shared

import org.bukkit.Material
import org.bukkit.inventory.MerchantInventory
import kotlin.math.min

internal fun MerchantInventory.quartzAmount(): Int = (0..1)
    .mapNotNull(::getItem)
    .filter { it.type == Material.QUARTZ }
    .sumOf { it.amount }

internal fun MerchantInventory.consumeQuartz(amount: Int): Boolean {
    if (quartzAmount() < amount) return false

    var remaining = amount

    for (slot in 0..1) {
        val item = getItem(slot) ?: continue
        if (item.type != Material.QUARTZ) continue

        val consumed = min(item.amount, remaining)
        item.amount -= consumed
        remaining -= consumed

        if (item.amount <= 0) setItem(slot, null)
        if (remaining == 0) return true
    }

    return false
}
