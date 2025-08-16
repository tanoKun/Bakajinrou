package com.github.tanokun.bakajinrou.plugin.interaction.participant.comingout.gui

import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.prefix.ComingOut
import com.github.tanokun.bakajinrou.game.participant.comingout.ComingOutHandler
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class ComingOutButton(
    private val translator: JinrouTranslator,
    private val comingOut: ComingOut,
    private val handler: ComingOutHandler,
    private val material: Material,
    private val mainScope: CoroutineScope
) : AbstractItem() {
    override fun getItemProvider(player: Player): ItemProvider {
        val coming = translator.translate(comingOut.translationKey, player.locale())
        val displayName = translator.translate(GameKeys.ComingOut.Gui.DESCRIPTION, player.locale(), coming)

        return ItemBuilder(material)
            .setDisplayName(AdventureComponentWrapper(displayName))
            .setItemFlags(ItemFlag.entries)

    }

    override fun handleClick(clickType: ClickType, clicker: Player, event: InventoryClickEvent) {
        clicker.closeInventory()

        mainScope.launch { handler.comingOut(clicker.uniqueId.asParticipantId(), comingOut) }
    }
}
