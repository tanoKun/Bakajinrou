package com.github.tanokun.bakajinrou.plugin.gui

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.gui.ability.UsableAbility
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.aqua
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.text
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
class AbilityGUI(ability: UsableAbility, user: Participant, participants: ParticipantScope.NonSpectators, method: GrantedMethod) {
    private val contents = participants
        .map {
            UsableAbilityButton(it, ability, user, method)
        }

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
            .setTitle(AdventureComponentWrapper(
                component {
                    text("能力使用") color aqua deco bold
                }
            ))
            .setViewer(player)
            .build()
            .open()
    }
}