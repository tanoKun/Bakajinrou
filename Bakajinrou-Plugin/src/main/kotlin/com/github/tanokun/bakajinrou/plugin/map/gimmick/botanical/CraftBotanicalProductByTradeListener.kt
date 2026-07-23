package com.github.tanokun.bakajinrou.plugin.map.gimmick.botanical

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.WanderingTrader
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.MerchantInventory
import org.bukkit.plugin.Plugin
import kotlin.math.min

class CraftBotanicalProductByTradeListener(
    plugin: Plugin,
    context: MapGimmickContext,
    crafting: Crafting,
    mainScope: CoroutineScope,
) : LifecycleEventListener(plugin, {
    register<InventoryClickEvent> { event ->
        val inventory = event.inventory as? MerchantInventory ?: return@register
        val merchant = inventory.merchant as? WanderingTrader ?: return@register
        if (BOTANICAL_MERCHANT_ENTITY_TAG !in merchant.scoreboardTags) return@register
        if (event.rawSlot != RESULT_SLOT) return@register

        event.isCancelled = true

        if (event.click !in supportedClicks) return@register
        if (!event.cursor.type.isAir) return@register

        val product = BotanicalMerchantProducts.productOf(event.currentItem) ?: return@register
        val player = event.whoClicked as? Player ?: return@register
        if (context.participant(player) == null) return@register

        if (inventory.quartzAmount() < BOTANICAL_MERCHANT_QUARTZ_COST) {
            player.sendMessage(
                Component.text(
                    "実行できません。クォーツが${BOTANICAL_MERCHANT_QUARTZ_COST}個必要です。",
                    NamedTextColor.RED,
                )
            )
            return@register
        }

        inventory.consumeQuartz(BOTANICAL_MERCHANT_QUARTZ_COST)
        mainScope.launch {
            crafting.craftMethod(
                player.uniqueId.asParticipantId(),
                product,
                CraftingStyle.SINGLE,
            )
        }
        player.playSound(player.location, Sound.ENTITY_WANDERING_TRADER_YES, 1.0f, 1.0f)
    }
}) {
    private companion object {
        const val RESULT_SLOT = 2

        val supportedClicks = setOf(ClickType.LEFT, ClickType.RIGHT)

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
