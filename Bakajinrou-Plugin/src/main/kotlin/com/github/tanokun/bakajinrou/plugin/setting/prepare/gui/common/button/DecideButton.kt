package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.common.button

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class DecideButton(private val decide: () -> Unit): AbstractItem() {

    override fun getItemProvider(): ItemProvider =
        ItemBuilder(Material.AMETHYST_SHARD)
            .setDisplayName("§a§l決定")
            .addLoreLines("§7設定を決定して次へ進みます。")

    override fun handleClick(clickType: ClickType, cilcker: Player, event: InventoryClickEvent) = decide()
}