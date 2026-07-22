package com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare

import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import com.github.tanokun.bakajinrou.plugin.presentation.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandlerType
import com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare.component.PreparedPlayerCategory
import com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare.component.PreparedPlayerTabComponent
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class PreparedGameTabRenderer(
    private val tabHandler: TabHandler,
    private val colorPallet: ColorPallet,
) {
    private val componentIds = hashMapOf<UUID, DummyUUID>()
    private var selectedParticipants: SelectedParticipants? = null

    fun show(selectedParticipants: SelectedParticipants) {
        check(this.selectedParticipants == null) { "Prepared game tab is already shown." }

        this.selectedParticipants = selectedParticipants
        tabHandler.createEngine(TabHandlerType.PreparedGame)

        Bukkit.getOnlinePlayers().forEach(::addComponentIfSelected)
        Bukkit.getOnlinePlayers().forEach { player ->
            tabHandler.joinEngine(TabHandlerType.PreparedGame, player)
        }
    }

    fun hide() {
        if (selectedParticipants == null) return

        Bukkit.getOnlinePlayers()
            .filter { player -> tabHandler.isJoinedTo(TabHandlerType.PreparedGame, player) }
            .forEach { player -> tabHandler.joinEngine(TabHandlerType.ShareInLobby, player) }

        tabHandler.deleteEngine(TabHandlerType.PreparedGame)
        selectedParticipants = null
        componentIds.clear()
    }

    fun onJoin(player: Player) {
        if (selectedParticipants == null) return

        addComponentIfSelected(player)
        tabHandler.joinEngine(TabHandlerType.PreparedGame, player)
    }

    fun onQuit(player: Player) {
        if (selectedParticipants == null) return

        val dummyUuid = componentIds.remove(player.uniqueId) ?: return
        tabHandler.editEngine(TabHandlerType.PreparedGame) {
            removeComponent(dummyUuid)
        }
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
}
