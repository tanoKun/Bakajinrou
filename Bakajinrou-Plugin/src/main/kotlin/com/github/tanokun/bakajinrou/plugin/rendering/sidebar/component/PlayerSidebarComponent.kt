package com.github.tanokun.bakajinrou.plugin.rendering.sidebar.component

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

data class PlayerSidebarComponent(
    private val componentProvider: (Player) -> Component,
) : SidebarComponent {
    override fun getComponent(player: Player): List<Component> = listOf(componentProvider(player))
}
