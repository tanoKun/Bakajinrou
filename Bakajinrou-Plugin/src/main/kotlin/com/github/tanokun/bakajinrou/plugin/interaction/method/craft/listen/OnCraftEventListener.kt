package com.github.tanokun.bakajinrou.plugin.interaction.method.craft.listen

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.plugin.Plugin

class OnCraftEventListener(
    plugin: Plugin, jinrouGame: JinrouGame, crafting: Crafting, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    fun getMaxCraftAmount(inventory: CraftingInventory): Int {
        var result = Int.MAX_VALUE
        for (item in inventory.matrix) {
            if (item == null || item.type.isAir) continue
            result = minOf(result, item.amount)
        }
        return if (result == Int.MAX_VALUE) 0 else result
    }

    register<CraftItemEvent> { event ->
        val crafter = jinrouGame.getParticipant(event.whoClicked.uniqueId.asParticipantId()) ?: return@register

        if (event.cursor.type != Material.AIR && !event.isShiftClick) return@register

        val repeat = if (event.isShiftClick) getMaxCraftAmount(inventory = event.inventory) else 1
        val style = if (event.isShiftClick) CraftingStyle.BULK else CraftingStyle.SINGLE

        repeat(repeat) {
            mainScope.launch { crafting.randomlyCrafting(crafter.participantId, style) }
            event.inventory.matrix.forEach { it?.amount -= 1 }
        }

        event.currentItem = null
    }
})