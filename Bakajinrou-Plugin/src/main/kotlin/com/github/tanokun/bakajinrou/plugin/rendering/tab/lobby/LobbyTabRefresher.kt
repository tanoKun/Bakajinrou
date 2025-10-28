package com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEntryComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType
import com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby.component.CandidateInLobbyFixedComponent
import com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby.component.SpectatorInLobbyFixedComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.world.level.GameType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class LobbyTabRefresher(
    private val gameSettings: GameSettings,
    private val tabHandler: TabHandler,
    private val jinrouTranslator: JinrouTranslator,
    private val playerProvider: BukkitPlayerProvider,
    coroutineScope: CoroutineScope,
): Listener {
    private val playerAttaches = HashMap<UUID, DummyUUID>()

    init {
        coroutineScope.launch {
            gameSettings.changedSpectators.collect {
                val dummyId = getDummyUuid(it.uuid)
                val targetPlayer = playerProvider.getAllowNull(ParticipantId(it.uuid)) ?: return@collect
                val gameType = GameType.byId(targetPlayer.gameMode.value)

                when (it) {
                    is GameSettings.ChangedSpectator.Added -> tabHandler.editEngine(TabHandlerType.ShareInLobby) {
                        val component = SpectatorInLobbyFixedComponent(dummyId, targetPlayer, gameType, jinrouTranslator)
                        updateComponent(component)
                    }

                    is GameSettings.ChangedSpectator.Removed -> tabHandler.editEngine(TabHandlerType.ShareInLobby) {
                        val component = CandidateInLobbyFixedComponent(dummyId, targetPlayer, gameType)
                        updateComponent(component)
                    }
                }
            }
        }
    }

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

        return if (player.uniqueId in gameSettings.spectators)
             SpectatorInLobbyFixedComponent(dummyId, player, gameType, jinrouTranslator)
         else
             CandidateInLobbyFixedComponent(dummyId, player, gameType)

    }
}