package com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare.gui.map

import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare.RecentSelectedMap
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare.SelectedMap
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare.gui.map.button.RandomlySelectMapButton
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.prepare.gui.map.button.SelectSingleMapButton
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.selects.select
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.green
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.gui.structure.Structure
import xyz.xenondevs.invui.window.Window

class SelectMapGui(private val player: Player, maps: List<GameMap>, recentSelectedMap: RecentSelectedMap) {
    private val candidates = maps.map { SelectSingleMapButton(it) }

    private val randomSelectButton = RandomlySelectMapButton(maps, recentSelectedMap)

    private val gui: Gui = PagedGui.ofItems(
        Structure(
            "m m m m m m m m m",
            "m m m m m m m m m",
            "m m m m m m m m r"
        )
            .addIngredient('m', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('r', randomSelectButton),
        candidates
    )

    suspend fun deferredSelection(): SelectedMap? {
        val deferredSelections = candidates.map { it.deferredSelection() } + randomSelectButton.deferredSelection()

        Window.single()
            .setGui(gui)
            .setTitle(
                AdventureComponentWrapper(
                    component {
                        text("マップ選択") color green deco bold
                    }
                )
            )
            .addCloseHandler {
                deferredSelections
                    .asSequence()
                    .forEach { if (!it.isCompleted) it.cancel() }
            }
            .open(player)

        return try {
            select {
                for (d in deferredSelections) d.onAwait { it }
            }
        }
        catch (_: CancellationException) { null }
    }
}