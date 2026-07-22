package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant

import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.common.button.DecideButton
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button.PlayerButton
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button.SelectByLoginTimeButton
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button.page.BackPageButton
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.button.page.NextPageButton
import kotlinx.coroutines.CompletableDeferred
import org.bukkit.Material
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.blue
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.SimpleItem
import xyz.xenondevs.invui.window.Window

class SelectParticipantGui(candidates: List<Player>) {
    private val candidates = ParticipantCandidates(candidates.map { it.uniqueId }.toSet())

    private val content = candidates
        .sortedByDescending { it.lastLogin }
        .map { PlayerButton(it, this@SelectParticipantGui.candidates) }

    private val deferred = CompletableDeferred<SelectedParticipants?>()

    private val gui: PagedGui<Item> = PagedGui.items()
        .setStructure(
            "o # x x x x x x x",
            "r # x x x x x x x",
            "_ # x x x x x x x",
            "_ # x x x x x x x",
            "_ # # # # # # # #",
            "b _ _ _ d _ _ _ n"
        )
        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('b', BackPageButton())
        .addIngredient('n', NextPageButton())
        .addIngredient('o', SelectByLoginTimeButton(this@SelectParticipantGui.candidates, ::updateContent))
        .addIngredient('d', DecideButton(::decide))
        .addIngredient('#', SimpleItem(ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
        .setContent(content)
        .build()

    suspend fun deferredSelection(player: Player): SelectedParticipants? {
        Window.single()
            .setGui(gui)
            .setTitle(
                AdventureComponentWrapper(
                    component {
                        text("参加者選択") color blue deco bold
                    }
                )
            )
            .addCloseHandler {
                if (!deferred.isCompleted) deferred.complete(null)
            }
            .open(player)

        return deferred.await()
    }

    private fun updateContent() = content.forEach { it.notifyWindows() }

    private fun decide() = deferred.complete(candidates.toSelected())

}