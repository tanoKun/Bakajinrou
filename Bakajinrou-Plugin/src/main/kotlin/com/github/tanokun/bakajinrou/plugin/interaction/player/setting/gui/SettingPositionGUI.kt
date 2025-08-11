package com.github.tanokun.bakajinrou.plugin.interaction.player.setting.gui

import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.common.setting.template.DistributionTemplates
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
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

class SettingPositionGUI(gameSettings: GameSettings, templates: DistributionTemplates, translator: JinrouTranslator) {
    private val contents = listOf(
        ChangePositionAmountButton(RequestedPositions.WOLF, gameSettings, Material.RED_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.MADMAN, gameSettings, Material.ORANGE_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.IDIOT, gameSettings, Material.WHITE_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.FORTUNE, gameSettings, Material.LIGHT_BLUE_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.MEDIUM, gameSettings, Material.MAGENTA_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.KNIGHT, gameSettings, Material.LIME_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.FOX, gameSettings, Material.PURPLE_BANNER, translator),
    )

    private val gui: Gui = PagedGui.ofItems(
        Structure(
            "# # # # # # # # #",
            "# a a a a a a a #",
            "# # # # # # # # t"
        )
            .addIngredient('a', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('t', ReplaceTemplateButton(gameSettings, templates, translator)),
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
}
