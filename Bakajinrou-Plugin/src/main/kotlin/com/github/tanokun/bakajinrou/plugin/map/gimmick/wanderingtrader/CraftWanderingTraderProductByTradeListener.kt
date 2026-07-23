package com.github.tanokun.bakajinrou.plugin.map.gimmick.wanderingtrader

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.shared.consumeQuartz
import com.github.tanokun.bakajinrou.plugin.map.gimmick.shared.quartzAmount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.WanderingTrader
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.MerchantInventory
import org.bukkit.plugin.Plugin
import kotlin.math.min

class CraftWanderingTraderProductByTradeListener(
    plugin: Plugin,
    context: MapGimmickContext,
    crafting: Crafting,
    mainScope: CoroutineScope,
) : LifecycleEventListener(plugin, {
    register<InventoryClickEvent> { event ->
        val inventory = event.inventory as? MerchantInventory ?: return@register
        val trader = inventory.merchant as? WanderingTrader ?: return@register
        if (WANDERING_TRADER_ENTITY_TAG !in trader.scoreboardTags) return@register
        if (event.rawSlot != RESULT_SLOT) return@register

        event.isCancelled = true

        if (event.click !in supportedClicks) return@register
        if (!event.isShiftClick && !event.cursor.type.isAir) return@register

        val product = WanderingTraderProducts.productOf(event.currentItem) ?: return@register
        val player = event.whoClicked as? Player ?: return@register
        if (context.participant(player) == null) return@register

        val availableCrafts = inventory.quartzAmount() / WANDERING_TRADER_QUARTZ_COST
        val craftCount = if (event.isShiftClick) availableCrafts else min(availableCrafts, 1)
        if (craftCount <= 0) {
            player.sendMessage(
                Component.text(
                    "実行できません。クォーツが${WANDERING_TRADER_QUARTZ_COST}個必要です。",
                    NamedTextColor.RED,
                )
            )
            return@register
        }

        inventory.consumeQuartz(craftCount * WANDERING_TRADER_QUARTZ_COST)
        val style = if (event.isShiftClick) CraftingStyle.BULK else CraftingStyle.SINGLE

        repeat(craftCount) {
            mainScope.launch {
                crafting.craftMethod(
                    player.uniqueId.asParticipantId(),
                    product,
                    style,
                )
            }
        }
        player.playSound(player.location, Sound.ENTITY_WANDERING_TRADER_YES, 1.0f, 1.0f)
    }
}) {
    private companion object {
        const val RESULT_SLOT = 2

        val supportedClicks = setOf(
            ClickType.LEFT,
            ClickType.RIGHT,
            ClickType.SHIFT_LEFT,
            ClickType.SHIFT_RIGHT,
        )

    }
}
