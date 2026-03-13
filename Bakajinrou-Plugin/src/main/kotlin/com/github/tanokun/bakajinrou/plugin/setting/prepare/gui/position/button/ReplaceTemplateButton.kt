package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.position.button

import com.github.tanokun.bakajinrou.plugin.common.setting.template.DistributionTemplates
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.position.PositionCandidates
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
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

class ReplaceTemplateButton(
    private val candidates: PositionCandidates,
    private val playerAmount: Int,
    private val templates: DistributionTemplates,
    private val updater: () -> Unit
): AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        val displayName = component {
            text("役職テンプレートの読み込み") color gray
        }

        return ItemBuilder(ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA))
            .setItemFlags(ItemFlag.entries)
            .setDisplayName(AdventureComponentWrapper(displayName))

    }

    override fun handleClick(clickType: ClickType, clicker: Player, event: InventoryClickEvent) {
        val template = templates.getPositions(playerAmount) ?: let {
            clicker.sendMessage(component {
                text("${playerAmount}人の役職テンプレートが見つかりませんでした。") color gray deco bold
            })

            clicker.playSound(Sound.sound(NamespacedKey("minecraft", "block.note_block.bass"), Sound.Source.PLAYER, 3.0f, 1.0f))

            return
        }


        template.forEach { (position, amount) ->
            candidates.updateAmount(position, amount)
        }

        updater()

        clicker.sendMessage(component {
            text("${playerAmount}人の役職テンプレートを読み込みました。") color gray deco bold
        })

        clicker.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
    }
}
