package com.github.tanokun.bakajinrou.plugin.presentation.sidebar

import fr.mrmicky.fastboard.adventure.FastBoard
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID

data class SidebarContent(
    val title: Component,
    val lines: List<Component>,
)

class Sidebar {
    private val boards = hashMapOf<UUID, FastBoard>()

    fun render(player: Player, content: SidebarContent) {
        val board = boards.getOrPut(player.uniqueId) { FastBoard(player) }
        board.updateTitle(content.title)
        board.updateLines(content.lines)
    }

    fun deleteAll() {
        boards.values.forEach(FastBoard::delete)
        boards.clear()
    }
}
