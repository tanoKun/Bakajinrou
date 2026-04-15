package com.github.tanokun.bakajinrou.plugin.setting.prepare.board

import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.rendering.sidebar.Sidebar
import com.github.tanokun.bakajinrou.plugin.rendering.sidebar.component.FixedSidebarComponent
import com.github.tanokun.bakajinrou.plugin.rendering.sidebar.component.PrefixAmountSidebarComponent
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import fr.mrmicky.fastboard.adventure.FastBoard
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.green
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text
import java.util.UUID

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

    private fun createOverviewBoard(player: Player) = createOverviewSidebar().createBoard(player)

    private fun createOverviewSidebar() = Sidebar(
        title = {
            component {
                text("ゲーム設定") color green deco bold
            }
        },
        components = buildList {
            add(FixedSidebarComponent(component {
                text("マップ: ") color gray deco bold
                text(selectedMap?.map?.mapName?.name ?: "未選択") color white deco bold
            }))

            add(FixedSidebarComponent(component {
                text("参加者数: ") color gray deco bold
                val count = selectedParticipants?.participants?.size ?: "未選択"
                text("$count") color white deco bold
            }))

            add(FixedSidebarComponent(component {
                text("役職分配: ") color gray deco bold
            }))

            selectedPositions.positions.forEach { (position, amount) ->
                add(PrefixAmountSidebarComponent(listOf(position.formatKey), amount, translator))
            }
        }
    )

    private fun createParticipantsBoard(player: Player) = createParticipantsSidebar().createBoard(player)

    private fun createParticipantsSidebar() = Sidebar(
        title = {
            component {
                text("参加者一覧") color green deco bold
            }
        },
        components = buildList {
            val participants = selectedParticipants?.participants

            if (participants == null) {
                add(FixedSidebarComponent(component {
                    text("未選択") color white deco bold
                }))

                return@buildList
            }

            participants.forEach { uuid ->
                add(FixedSidebarComponent(component {
                    val participantName = PlayerNameCache.get(uuid)

                    text("・") color gray deco bold
                    text(participantName ?: "") color white deco bold
                }))
            }
        }
    )

    fun deleteAll() {
        boards.values.forEach(FastBoard::delete)
        boards.clear()
    }
}
