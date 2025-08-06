/*
package com.github.tanokun.bakajinrou.plugin.gui.setting

import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.setting.template.DistributionTemplates
import org.bukkit.Material
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gold
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.gui.structure.Structure
import xyz.xenondevs.invui.window.Window

class SettingPositionGUI(gameSettings: GameSettings, templates: DistributionTemplates) {
    private val contents = listOf(
        ChangePositionAmountButton(Positions.Wolf, gameSettings, Material.RED_BANNER),
        ChangePositionAmountButton(Positions.Madman, gameSettings, Material.ORANGE_BANNER),
        ChangePositionAmountButton(Positions.Idiot, gameSettings, Material.WHITE_BANNER),
        ChangePositionAmountButton(Positions.Fortune, gameSettings, Material.LIGHT_BLUE_BANNER),
        ChangePositionAmountButton(Positions.Medium, gameSettings, Material.MAGENTA_BANNER),
        ChangePositionAmountButton(Positions.Knight, gameSettings, Material.LIME_BANNER),
        ChangePositionAmountButton(Positions.Fox, gameSettings, Material.PURPLE_BANNER),
    )

    private val gui: Gui = PagedGui.ofItems(
        Structure(
            "# # # # # # # # #",
            "# a a a a a a a #",
            "# # # # # # # # t"
        )
            .addIngredient('a', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('t', ReplaceTemplateButton(gameSettings, templates)),
        contents
    )

    fun open(player: Player) {
        Window.single()
            .setGui(gui)
            .setTitle(AdventureComponentWrapper(
                component {
                    text("役職設定") color gold deco bold
                }
            ))
            .setViewer(player)
            .build()
            .open()
    }
}*/
