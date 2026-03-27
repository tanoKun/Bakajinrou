package com.github.tanokun.bakajinrou.plugin.setting.prepare.board

import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import fr.mrmicky.fastboard.adventure.FastBoard
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
import java.util.*

data class PreparedGameBoardRenderer(
    val selectedMap: SelectedMap?,
    val selectedParticipants: SelectedParticipants?,
    val selectedPositions: SelectedPositions,
    private val translator: JinrouTranslator,
) {

    private val boards = hashMapOf<UUID, FastBoard>()

    fun renderGameOverview(player: Player) {
        boards.remove(player.uniqueId)?.delete()
        boards[player.uniqueId] = createOverviewBoard(player)
    }

    fun renderParticipants(player: Player) {
        boards.remove(player.uniqueId)?.delete()
        boards[player.uniqueId] = createParticipantsBoard(player)
    }

    private fun createOverviewBoard(player: Player) = FastBoard(player).apply {
        updateTitle(component {
            text("ゲーム設定") color green deco bold
        })

        val lines = mutableListOf<Component>()
        lines.add(component {
            text("マップ: ") color gray deco bold
            text(selectedMap?.map?.mapName?.name ?: "未選択") color white deco bold
        })

        lines.add(component {
            text("参加者数: ") color gray deco bold
            val count = selectedParticipants?.participants?.size ?: "未選択"
            text("$count") color white deco bold
        })

        lines.add(component {
            text("役職分配: ") color gray deco bold
        })

        selectedPositions.positions.forEach { (position, amount) ->
            lines.add(getPositionLine(position, amount, player))
        }

        updateLines(lines)
    }

    private fun createParticipantsBoard(player: Player) = FastBoard(player).apply {
        updateTitle(component {
            text("参加者一覧") color green deco bold
        })

        val lines = mutableListOf<Component>()
        val participants = selectedParticipants?.participants

        if (participants == null) {
            lines.add(component {
                text("未選択") color white deco bold
            })

            updateLines(lines)
            return@apply
        }

        participants.forEach { uuid ->
            val participantName = PlayerNameCache.get(uuid)
            lines.add(component {
                text("・") color gray deco bold
                text(participantName ?: "") color white deco bold
            })
        }

        updateLines(lines)
    }

    private fun getPositionLine(position: RequestedPositions, amount: Int, player: Player): Component {
        return component {
            text(" 「") color gray deco bold
            raw { translator.translate(position.formatKey, player.locale()) } deco bold
            text("」→ ") color gray deco bold
            text("${amount}人") color white deco bold
        }
    }

    fun deleteAll() {
        boards.values.forEach(FastBoard::delete)
        boards.clear()
    }
}