package com.github.tanokun.bakajinrou.plugin.rendering.sidebar.component

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface SidebarComponent {
    fun getComponent(player: Player): List<Component>
}