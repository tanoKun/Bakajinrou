package com.github.tanokun.bakajinrou.plugin.gui.setting

import com.github.tanokun.bakajinrou.plugin.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.setting.template.DistributionTemplates
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class ReplaceTemplateButton(private val gameSettings: GameSettings, private val templates: DistributionTemplates): AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        val displayName = component {
            text("役職テンプレートの読み込み") color gray
        }

        return ItemBuilder(ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA))
            .setItemFlags(ItemFlag.entries)
            .setDisplayName(AdventureComponentWrapper(displayName))

    }

    override fun handleClick(clickType: ClickType, clicker: Player, event: InventoryClickEvent) {
        val amount = gameSettings.candidates.size
        val template = templates.getPositions(amount) ?: let {
            clicker.sendMessage(component {
                text("${amount}人の役職テンプレートが見つかりませんでした。") color gray deco bold
            })

            clicker.playSound(Sound.sound(NamespacedKey("minecraft", "block.note_block.bass"), Sound.Source.PLAYER, 3.0f, 1.0f))
            clicker.closeInventory()

            return
        }

        template.forEach { (position, amount) ->
            gameSettings.updateAmount(position, amount)
        }

        clicker.sendMessage(component {
            text("${amount}人の役職テンプレートを読み込みました。") color gray deco bold
        })
        clicker.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
        SettingPositionGUI(gameSettings, templates).open(clicker)
    }
}