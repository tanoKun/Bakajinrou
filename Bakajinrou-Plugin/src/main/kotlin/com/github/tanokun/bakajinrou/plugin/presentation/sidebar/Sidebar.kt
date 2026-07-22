package com.github.tanokun.bakajinrou.plugin.presentation.sidebar

import fr.mrmicky.fastboard.adventure.FastBoard
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.time.Duration

data class SidebarContent(
    val title: Component,
    val lines: List<Component>,
)

data class SidebarPage(
    val displayDuration: Duration,
    val content: (Player) -> SidebarContent,
)

class Sidebar {
    private val boards = hashMapOf<UUID, FastBoard>()

    fun render(player: Player, content: SidebarContent) {
        val board = boards.getOrPut(player.uniqueId) { FastBoard(player) }
        board.updateTitle(content.title)
        board.updateLines(content.lines)
    }

    suspend fun cycle(
        viewers: () -> Collection<Player>,
        pages: List<SidebarPage>,
    ) {
        require(pages.isNotEmpty()) { "Sidebar pages must not be empty." }

        try {
            while (currentCoroutineContext().isActive) {
                pages.forEach { page ->
                    viewers().forEach { player -> render(player, page.content(player)) }
                    delay(page.displayDuration)
                }
            }
        } finally {
            deleteAll()
        }
    }

    fun deleteAll() {
        boards.values.forEach(FastBoard::delete)
        boards.clear()
    }
}
