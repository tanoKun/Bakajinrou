package com.github.tanokun.bakajinrou.plugin.interaction.method.ability.using.gui

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

 * @param ability GUIで使用するアビリティ（使用者が誰かに対して使用できる能力）
 * @param user アビリティの使用者
 * @param participants 能力の使用対象になり得る候補一覧（観戦者を除外した生存者など）
 * @param method アビリティの使用方法（クリック時にどのように対象に能力を付与するかなど）
 */
class AbilityGUI(
    private val translator: JinrouTranslator,
    participants: ParticipantScope.NonSpectators,
    description: GameKeys.Gui.Using,
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
            .setTitle(AdventureComponentWrapper(translator.translate(GameKeys.Gui.TITLE, player.locale())))
            .setViewer(player)
            .build()
            .open()
    }
}
