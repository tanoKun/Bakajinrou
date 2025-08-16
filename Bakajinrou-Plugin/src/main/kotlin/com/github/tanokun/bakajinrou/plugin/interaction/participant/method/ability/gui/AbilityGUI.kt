package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.gui

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import org.bukkit.entity.Player
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.structure.Markers
import xyz.xenondevs.invui.gui.structure.Structure
import xyz.xenondevs.invui.window.Window


/**
 * プレイヤーが使用可能な能力を対象参加者に対して選択するインターフェース提供します。
 */
class AbilityGUI(
    private val translator: JinrouTranslator,
    participants: ParticipantScope.NonSpectators,
    description: GameKeys.Ability.Gui.Using,
    onClick: (clicker: ParticipantId, target: ParticipantId) -> Unit,
) {
    private val contents = participants
        .map { UsableAbilityButton(translator, it.participantId, description, onClick) }

    private val gui: Gui = PagedGui.ofItems(
        Structure(
            "x x x x x x x x x",
            "x x x x x x x x x",
            "x x x x x x x x x"
        ).addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL),
        contents
    )

    fun open(player: Player) {
        Window.single()
            .setGui(gui)
            .setTitle(AdventureComponentWrapper(translator.translate(GameKeys.Ability.Gui.TITLE, player.locale())))
            .setViewer(player)
            .build()
            .open()
    }
}
