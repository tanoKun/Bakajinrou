package com.github.tanokun.bakajinrou.plugin.rendering.sidebar.prepare

import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.adapter.getSelectedData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import java.util.UUID

class RenderingSidebarAdapter(
    private val scope: TopCoroutineScope,
    private val translator: JinrouTranslator,
) : Listener {
    private var animateJob: Job? = null
    private var viewer: UUID? = null

    @EventHandler
    fun animateOverview(event: PlayerItemHeldEvent) {
        if (event.player.uniqueId != viewer && animateJob != null) return

        animateJob?.cancel()

        val item = event.player.inventory.getItem(event.newSlot) ?: return
        if (item.type != Material.FILLED_MAP) return

        val (map, participants, positions) = getSelectedData(item) ?: return
        val renderer = PreparedGameSidebarRenderer(map, participants, positions, translator)

        viewer = event.player.uniqueId
        animateJob = scope.launch {
            renderer.cycle {
                if (isViewingPreparedGame()) Bukkit.getOnlinePlayers()
                else {
                    animateJob?.cancel()
                    emptyList()
                }
            }
        }.also { job ->
            job.invokeOnCompletion {
                if (animateJob === job) {
                    animateJob = null
                    viewer = null
                }
            }
        }
    }

    private fun isViewingPreparedGame(): Boolean {
        val player = viewer?.let(Bukkit::getPlayer) ?: return false
        val item = player.inventory.itemInMainHand

        return item.type == Material.FILLED_MAP && getSelectedData(item) != null
    }
}
