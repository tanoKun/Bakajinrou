package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.position

import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.common.setting.template.DistributionTemplates
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.common.button.DecideButton
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.position.button.ChangePositionAmountButton
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.position.button.ReplaceTemplateButton
import kotlinx.coroutines.CompletableDeferred
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
import xyz.xenondevs.invui.window.Window

class SelectPositionGui(
    templates: DistributionTemplates,
    translator: JinrouTranslator,
    playerAmount: Int
) {
    private val candidates = PositionCandidates()

    val deferred = CompletableDeferred<SelectedPositions?>()

    private val contents = listOf(
        ChangePositionAmountButton(RequestedPositions.WOLF, candidates, Material.RED_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.MADMAN, candidates, Material.ORANGE_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.IDIOT, candidates, Material.WHITE_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.FORTUNE, candidates, Material.LIGHT_BLUE_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.MEDIUM, candidates, Material.MAGENTA_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.KNIGHT, candidates, Material.LIME_BANNER, translator),
        ChangePositionAmountButton(RequestedPositions.FOX, candidates, Material.PURPLE_BANNER, translator),
    )

    private val gui: Gui = PagedGui.items()
        .setStructure(
            "# # # # # # # # #",
            "# a a a a a a a #",
            "# # # # d # # # t"
        )
        .addIngredient('a', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('d', DecideButton(::decide))
        .addIngredient('t', ReplaceTemplateButton(candidates, playerAmount, templates, ::updateContent))
        .setContent(contents)
        .build()

    suspend fun deferredSelection(player: Player): SelectedPositions? {
        Window.single()
            .setGui(gui)
            .setTitle(AdventureComponentWrapper(
                component {
                    text("役職設定") color gold deco bold
                }
            ))
            .addCloseHandler {
                if (!deferred.isCompleted) deferred.complete(null)
            }
            .open(player)

        return deferred.await()
    }

    private fun updateContent() = contents.forEach { it.notifyWindows() }

    private fun decide() = deferred.complete(candidates.toSelected())
}
