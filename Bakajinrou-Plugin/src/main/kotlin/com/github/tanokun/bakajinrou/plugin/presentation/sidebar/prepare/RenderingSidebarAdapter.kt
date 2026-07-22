package com.github.tanokun.bakajinrou.plugin.presentation.sidebar.prepare

import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.getSelectedData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import java.util.UUID

class RenderingSidebarAdapter(
    private val translator: JinrouTranslator,
) : Listener {
    private var renderer: PreparedGameSidebarRenderer? = null
    private var viewer: UUID? = null

    @EventHandler
    fun renderOverview(event: PlayerItemHeldEvent) {
        if (event.player.uniqueId != viewer && renderer != null) return

        renderer?.deleteAll()
        renderer = null
        viewer = null

        val item = event.player.inventory.getItem(event.newSlot) ?: return
        if (item.type != Material.FILLED_MAP) return

        val (map, participants, positions) = getSelectedData(item) ?: return
        val newRenderer = PreparedGameSidebarRenderer(map, participants, positions, translator)

        viewer = event.player.uniqueId
        renderer = newRenderer
        Bukkit.getOnlinePlayers().forEach(newRenderer::renderGameOverview)
    }
}
