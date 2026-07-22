package com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare

import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import com.github.tanokun.bakajinrou.plugin.presentation.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandlerType
import com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare.component.PreparedPlayerCategory
import com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare.component.PreparedPlayerTabComponent
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.getSelectedData
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PreparedGameTabRefresher(
    private val scope: TopCoroutineScope,
    private val tabHandler: TabHandler,
    private val colorPallet: ColorPallet,
) : Listener {
    private val componentIds = hashMapOf<UUID, DummyUUID>()
    private var selectedParticipants: SelectedParticipants? = null
    private var refreshJob: Job? = null
    private var viewer: UUID? = null

    @EventHandler
    fun updateTab(event: PlayerItemHeldEvent) {
        if (event.player.uniqueId != viewer && refreshJob?.isActive == true) return

        val item = event.player.inventory.getItem(event.newSlot)
        val participants = item
            ?.takeIf { it.type == Material.FILLED_MAP }
            ?.let(::getSelectedData)
            ?.second

        viewer = event.player.uniqueId
        replaceTab(event.player.uniqueId, participants)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        scope.launch {
            delay(100.milliseconds)
            if (selectedParticipants == null) return@launch

            addComponentIfSelected(event.player)
            tabHandler.joinEngine(TabHandlerType.PreparedGame, event.player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (selectedParticipants == null) return

        val dummyUuid = componentIds.remove(event.player.uniqueId) ?: return
        tabHandler.editEngine(TabHandlerType.PreparedGame) {
            removeComponent(dummyUuid)
        }
    }

    private fun replaceTab(ownerId: UUID, participants: SelectedParticipants?) {
        val previous = refreshJob
        val job = scope.launch {
            previous?.cancelAndJoin()
            if (participants == null) return@launch

            show(participants)
            try {
                while (coroutineContext.isActive && isViewingPreparedGame(ownerId)) {
                    delay(1.seconds)
                }
            } finally {
                hide()
            }
        }

        refreshJob = job
        job.invokeOnCompletion {
            if (refreshJob === job) {
                refreshJob = null
                viewer = null
            }
        }
    }

    private fun show(participants: SelectedParticipants) {
        selectedParticipants = participants
        tabHandler.createEngine(TabHandlerType.PreparedGame)

        Bukkit.getOnlinePlayers().forEach(::addComponentIfSelected)
        Bukkit.getOnlinePlayers().forEach { player ->
            tabHandler.joinEngine(TabHandlerType.PreparedGame, player)
        }
    }

    private fun hide() {
        if (selectedParticipants == null) return

        Bukkit.getOnlinePlayers()
            .filter { player -> tabHandler.isJoinedTo(TabHandlerType.PreparedGame, player) }
            .forEach { player -> tabHandler.joinEngine(TabHandlerType.ShareInLobby, player) }

        tabHandler.deleteEngine(TabHandlerType.PreparedGame)
        selectedParticipants = null
        componentIds.clear()
    }

    private fun addComponentIfSelected(player: Player) {
        val category = categoryOf(player.uniqueId) ?: return
        if (componentIds.containsKey(player.uniqueId)) return

        val dummyUuid = DummyUUID.random()
        componentIds[player.uniqueId] = dummyUuid
        tabHandler.editEngine(TabHandlerType.PreparedGame) {
            addComponent(PreparedPlayerTabComponent(dummyUuid, player, category, colorPallet))
        }
    }

    private fun categoryOf(playerId: UUID): PreparedPlayerCategory? {
        val selected = selectedParticipants ?: return null
        return when (playerId) {
            in selected.participants -> PreparedPlayerCategory.PARTICIPANT
            in selected.spectators -> PreparedPlayerCategory.SPECTATOR
            else -> null
        }
    }

    private fun isViewingPreparedGame(playerId: UUID): Boolean {
        val player = Bukkit.getPlayer(playerId) ?: return false
        val item = player.inventory.itemInMainHand
        return item.type == Material.FILLED_MAP && getSelectedData(item) != null
    }
}
