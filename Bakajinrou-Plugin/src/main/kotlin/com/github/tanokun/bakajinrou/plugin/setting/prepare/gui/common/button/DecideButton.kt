package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.common.button

import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.ParticipantCandidates
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button.WHEN_BE_HEAD_TEXTURE
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.AbstractItemBuilder
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.builder.SkullBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class DecideButton(private val decide: () -> Unit): AbstractItem() {

    override fun getItemProvider(): ItemProvider =
        ItemBuilder(Material.AMETHYST_SHARD)
            .setDisplayName("§a§l決定")
            .addLoreLines("§7設定を決定して次へ進みます。")

    override fun handleClick(clickType: ClickType, cilcker: Player, event: InventoryClickEvent) = decide()
}