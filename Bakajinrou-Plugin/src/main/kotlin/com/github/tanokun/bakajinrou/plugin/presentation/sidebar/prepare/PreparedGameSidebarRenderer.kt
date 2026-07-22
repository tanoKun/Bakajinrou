package com.github.tanokun.bakajinrou.plugin.presentation.sidebar.prepare

import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.presentation.sidebar.Sidebar
import com.github.tanokun.bakajinrou.plugin.presentation.sidebar.SidebarContent
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.green
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text

data class PreparedGameSidebarRenderer(
    val selectedMap: SelectedMap?,
    val selectedParticipants: SelectedParticipants?,
    val selectedPositions: SelectedPositions,
    private val translator: JinrouTranslator,
) {
    private val sidebar = Sidebar()

    fun renderGameOverview(player: Player) = sidebar.render(player, createOverview(player))

    private fun createOverview(player: Player): SidebarContent {
        val lines = mutableListOf<Component>()
        lines.add(component {
            text("マップ: ") color gray deco bold
            text(selectedMap?.map?.mapName?.name ?: "未選択") color white deco bold
        })
        lines.add(component { text("役職分配: ") color gray deco bold })
        selectedPositions.positions.forEach { (position, amount) ->
            lines.add(createPositionLine(position, amount, player))
        }

        return SidebarContent(
            title = component { text("ゲーム設定") color green deco bold },
            lines = lines,
        )
    }

    private fun createPositionLine(position: RequestedPositions, amount: Int, player: Player) = component {
        text(" 「") color gray deco bold
        raw { translator.translate(position.formatKey, player.locale()) } deco bold
        text("」→ ") color gray deco bold
        text("${amount}人") color white deco bold
    }

    fun deleteAll() = sidebar.deleteAll()
}
