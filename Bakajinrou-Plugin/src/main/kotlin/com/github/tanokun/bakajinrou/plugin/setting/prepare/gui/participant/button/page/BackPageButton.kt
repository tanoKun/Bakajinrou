package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button.page

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.builder.SkullBuilder
import xyz.xenondevs.invui.item.impl.controlitem.PageItem

private const val WOODEN_LEFT_SKULL_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVkNzg4MjI1NzYzMTdiMDQ4ZWVhOTIyMjdjZDg1ZjdhZmNjNDQxNDhkY2I4MzI3MzNiYWNjYjhlYjU2ZmExIn19fQ=="

class BackPageButton: PageItem(true) {

    override fun getItemProvider(gui: PagedGui<*>): ItemProvider {
        if (!gui.hasPreviousPage()) return ItemBuilder(Material.AIR)

        return SkullBuilder(SkullBuilder.HeadTexture(WOODEN_LEFT_SKULL_VALUE))
            .setDisplayName("§l前のページに行く")
            .addLoreLines("§7${gui.currentPage} ページ")
    }

    override fun handleClick(clickType: ClickType, player: Player, e: InventoryClickEvent) {
        if (!gui.hasPreviousPage()) return

        player.playSound(player, Sound.BLOCK_SHULKER_BOX_OPEN, 1.0F, 1.0F)
        gui.goBack()
    }
}