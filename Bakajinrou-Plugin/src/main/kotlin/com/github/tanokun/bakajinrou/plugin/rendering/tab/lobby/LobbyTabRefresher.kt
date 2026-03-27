package com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby

import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType
import com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby.component.PlayerInLobbyFixedComponent
import kotlinx.coroutines.delay
import net.minecraft.world.level.GameType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class LobbyTabRefresher(
    private val tabHandler: TabHandler,
): Listener {
    private val playerAttaches = HashMap<UUID, DummyUUID>()

    @EventHandler
    suspend fun onJoin(e: PlayerJoinEvent) {
        delay(50)

        tabHandler.joinEngine(TabHandlerType.ShareInLobby, e.player)
        tabHandler.editEngine(TabHandlerType.ShareInLobby) {
            val gameType = GameType.byId(e.player.gameMode.value)
            addComponent(getComponent(e.player, gameType))
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val dummyId = getDummyUuid(e.player.uniqueId)

        tabHandler.editEngine(TabHandlerType.ShareInLobby) {
            removeComponent(dummyId)
        }
    }

    @EventHandler
    fun onChangeGameMode(e: PlayerGameModeChangeEvent) {
        tabHandler.editEngine(TabHandlerType.ShareInLobby) {
            updateComponent(getComponent(e.player, GameType.byId(e.newGameMode.value)))
        }
    }

    private fun getDummyUuid(playerUuid: UUID): DummyUUID {
        return playerAttaches.computeIfAbsent(playerUuid) { DummyUUID.random() }
    }

    private fun getComponent(player: Player, gameType: GameType): TabEntryComponent {
        val dummyId = getDummyUuid(player.uniqueId)

        return PlayerInLobbyFixedComponent(dummyId, player, gameType)
    }
}