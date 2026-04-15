package com.github.tanokun.bakajinrou.plugin.rendering.sidebar

import com.github.tanokun.bakajinrou.plugin.rendering.sidebar.component.SidebarComponent
import fr.mrmicky.fastboard.adventure.FastBoard
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

data class Sidebar(
	val title: (Player) -> Component,
	val components: List<SidebarComponent>,
) {
	fun createBoard(player: Player) = FastBoard(player).apply {
		updateTitle(title(player))
		updateLines(components.flatMap { it.getComponent(player) })
	}
}