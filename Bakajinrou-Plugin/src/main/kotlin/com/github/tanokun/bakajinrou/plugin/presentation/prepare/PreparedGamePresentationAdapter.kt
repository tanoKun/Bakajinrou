package com.github.tanokun.bakajinrou.plugin.presentation.prepare

import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.presentation.sidebar.prepare.PreparedGameSidebarRenderer
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare.PreparedGameTabRenderer
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.getSelectedData
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

class PreparedGamePresentationAdapter(
    private val scope: TopCoroutineScope,
    private val translator: JinrouTranslator,
    tabHandler: TabHandler,
    colorPallet: ColorPallet,
) : Listener {
    private val tabRenderer = PreparedGameTabRenderer(tabHandler, colorPallet)
    private var presentationJob: Job? = null
    private var viewer: UUID? = null

    @EventHandler
    fun updatePresentation(event: PlayerItemHeldEvent) {
        if (event.player.uniqueId != viewer && presentationJob?.isActive == true) return

        val item = event.player.inventory.getItem(event.newSlot)
        val selectedData = item
            ?.takeIf { it.type == Material.FILLED_MAP }
            ?.let(::getSelectedData)

        viewer = event.player.uniqueId
        replacePresentation(event.player, selectedData)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        scope.launch {
            delay(100)
            tabRenderer.onJoin(event.player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        tabRenderer.onQuit(event.player)
    }

    private fun replacePresentation(
        owner: Player,
        selectedData: Triple<
            SelectedMap,
            SelectedParticipants,
            SelectedPositions,
        >?,
    ) {
        val previous = presentationJob
        val job = scope.launch {
            previous?.cancelAndJoin()
            if (selectedData == null) return@launch

            val (map, participants, positions) = selectedData
            val sidebarRenderer = PreparedGameSidebarRenderer(map, participants, positions, translator)

            tabRenderer.show(participants)
            try {
                coroutineScope {
                    val sidebarJob = launch {
                        sidebarRenderer.cycle { Bukkit.getOnlinePlayers() }
                    }

                    while (coroutineContext.isActive && isViewingPreparedGame(owner.uniqueId)) {
                        delay(1.seconds)
                    }
                    sidebarJob.cancelAndJoin()
                }
            } finally {
                sidebarRenderer.deleteAll()
                tabRenderer.hide()
            }
        }

        presentationJob = job
        job.invokeOnCompletion {
            if (presentationJob === job) {
                presentationJob = null
                viewer = null
            }
        }
    }

    private fun isViewingPreparedGame(playerId: UUID): Boolean {
        val player = Bukkit.getPlayer(playerId) ?: return false
        val item = player.inventory.itemInMainHand
        return item.type == Material.FILLED_MAP && getSelectedData(item) != null
    }
}
