package com.github.tanokun.bakajinrou.plugin.interaction.method.crafting

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class OnCraftEventListener(
    plugin: Plugin, crafting: Crafting, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    fun getMaxCraftAmount(inventory: CraftingInventory): Int {
        var result = Int.MAX_VALUE
        for (item in inventory.matrix) {
            if (item == null || item.type.isAir) continue
            result = minOf(result, item.amount)
        }
        return if (result == Int.MAX_VALUE) 0 else result
    }

    fun isRandomlyCrafting(item: ItemStack?): Boolean = item?.type == Material.QUARTZ_BLOCK
    fun isCraftingInherentMethods(item: ItemStack?): Boolean = item?.type == Material.END_CRYSTAL

    register<CraftItemEvent> { event ->
        val item = event.currentItem
        if (!isRandomlyCrafting(item) && !isCraftingInherentMethods(item)) return@register

        val crafterId = event.whoClicked.uniqueId.asParticipantId()

        if (event.cursor.type != Material.AIR && !event.isShiftClick) return@register

        val repeat = if (event.isShiftClick) getMaxCraftAmount(inventory = event.inventory) else 1
        val style = if (event.isShiftClick) CraftingStyle.BULK else CraftingStyle.SINGLE

        repeat(repeat) {
            if (isRandomlyCrafting(item))
                mainScope.launch { crafting.randomlyCraftMethod(crafterId, style) }

            if (isCraftingInherentMethods(item))
                mainScope.launch { crafting.craftInherentMethods(crafterId, style) }

            event.inventory.matrix.forEach { it?.amount -= 1 }
        }

        event.currentItem = null
    }

    register<PrepareItemCraftEvent> { event ->
        val item = event.recipe?.result

        if (!isCraftingInherentMethods(item)) return@register
        if (crafting.hasInherentMethods(event.view.player.uniqueId.asParticipantId())) return@register

        event.inventory.result = ItemStack.of(Material.AIR)
    }
})