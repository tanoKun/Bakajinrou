package com.github.tanokun.bakajinrou.plugin.setting.start.adapter

import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import com.github.tanokun.bakajinrou.plugin.setting.start.GameStarter
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.*
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem
import xyz.xenondevs.invui.window.Window

class FinalDecisionGui(
    private val map: SelectedMap,
    private val participants: SelectedParticipants,
    private val positions: SelectedPositions,
    private val starter: GameStarter
) {
    private val gui: Gui = Gui.normal()
        .setStructure(
            "# # # # # # # # #",
            "# # # # y # # # #",
            "# # # # # # # # #"
        )
        .addIngredient('y', ApplyButton())
        .build()

    fun open(player: Player) {
        Window.single()
            .setGui(gui)
            .setTitle(AdventureComponentWrapper(
                component {
                    text("最終確認") color gold deco bold
                }
            ))
            .open(player)
    }

    private inner class ApplyButton : AbstractItem() {
        override fun getItemProvider(player: Player): ItemProvider {
            val displayName = component {
                text("ゲームを開始します") color green deco bold
            }

            return ItemBuilder(ItemStack(Material.GREEN_WOOL))
                .setDisplayName(AdventureComponentWrapper(displayName))

        }

        override fun handleClick(clickType: ClickType, clicker: Player, event: InventoryClickEvent) {
            val result = starter.buildGameSession(map, participants, positions)

            clicker.closeInventory()

            when (result) {
                is GameStarter.GameBuildResult.Failure -> {
                    clicker.sendMessage(
                        component {
                            text("ゲームの開始に失敗しました: ") color gray
                            text(result.reason) color red deco bold
                        }
                    )
                }
                is GameStarter.GameBuildResult.SucceedCreation -> {
                    result.gameSession.launch()

                    clicker.inventory.setItemInMainHand(ItemStack(Material.AIR))
                }
            }
        }
    }
}
