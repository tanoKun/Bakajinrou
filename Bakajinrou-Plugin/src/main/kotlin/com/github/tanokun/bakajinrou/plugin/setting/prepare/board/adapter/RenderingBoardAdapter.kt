package com.github.tanokun.bakajinrou.plugin.setting.prepare.board.adapter

import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.prepare.board.PreparedGameBoardRenderer
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.adapter.getSelectedData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import java.util.*

class RenderingBoardAdapter(private val scope: TopCoroutineScope, private val jinrouTranslator: JinrouTranslator): Listener {

    private var animateJob: Job? = null

    private var viewer: UUID? = null

    @EventHandler
    fun animateOverview(e: PlayerItemHeldEvent) {
        if (e.player.uniqueId != viewer && animateJob != null) return

        animateJob?.cancel()

        val item = e.player.inventory.getItem(e.newSlot) ?: return
        if (item.type != Material.FILLED_MAP) return

        val (map, participants, positions) = getSelectedData(item) ?: return

        val preparedGameBoardRenderer = PreparedGameBoardRenderer(
            selectedMap = map,
            selectedParticipants = participants,
            selectedPositions = positions,
            translator = jinrouTranslator
        )

        viewer = e.player.uniqueId

        animateJob = scope.launch {
            while (this.isActive) {
                Bukkit.getOnlinePlayers().forEach { preparedGameBoardRenderer.renderGameOverview(it) }
                delay(5000L)
                Bukkit.getOnlinePlayers().forEach { preparedGameBoardRenderer.renderParticipants(it) }
                delay(2000L)
            }
        }

        animateJob?.invokeOnCompletion {
            preparedGameBoardRenderer.deleteAll()
            animateJob = null
            viewer = null
        }
    }
}