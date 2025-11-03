package com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare.gui.map.button

import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare.SelectedMap
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

private val lore = listOf(
    component {
        text("「左クリック」") color white deco bold
        text("で") color gray
        text("選択") color gray deco bold
        text("します。") color gray
    }
).map { AdventureComponentWrapper(it) }

class SelectSingleMapButton(private val map: GameMap): AbstractItem() {
    private val deferred = CompletableDeferred<SelectedMap>()

    override fun getItemProvider(): ItemProvider =
        ItemBuilder(map.icon)
            .setDisplayName(map.mapName.name)
            .addLoreLines(lore)

    override fun handleClick(type: ClickType, clicker: Player, e: InventoryClickEvent) {
        if (!type.isLeftClick) return

        deferred.complete(SelectedMap(map))
    }

    fun deferredSelection() = deferred as Deferred<SelectedMap>
}