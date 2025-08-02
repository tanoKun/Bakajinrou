package com.github.tanokun.bakajinrou.plugin.observer

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import kotlinx.coroutines.launch
import net.minecraft.Optionull
import net.minecraft.network.chat.RemoteChatSession
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.craftbukkit.entity.CraftPlayer
import java.util.*
import kotlin.coroutines.CoroutineContext

class ParticipantStateObserver(
    participants: ParticipantScope.All,
    controller: JinrouGameController,
    uiContext: CoroutineContext,
    asyncContext: CoroutineContext,
) {
    init {
        participants.forEach {
            it.observeState(state = ParticipantStates.DEAD, scope = controller.scope, context = asyncContext) {
                controller.scope.launch(uiContext) {
                    val participantPlayer = Bukkit.getPlayer(it.uniqueId) ?: return@launch

                    participantPlayer.gameMode = GameMode.SPECTATOR

                    val entries = Bukkit.getOnlinePlayers()
                        .filter { p -> p !== participantPlayer }
                        .map { player -> createPacketEntry(player as CraftPlayer) }

                    if (entries.isEmpty()) return@launch

                    (participantPlayer as? CraftPlayer)?.handle?.connection?.send(
                        ClientboundPlayerInfoUpdatePacket(EnumSet.of(
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE
                        ), entries)
                    )
                }
            }
        }
    }

    private fun createPacketEntry(player: CraftPlayer): ClientboundPlayerInfoUpdatePacket.Entry {
        val handle = player.handle
        return ClientboundPlayerInfoUpdatePacket.Entry(
            handle.uuid,
            handle.gameProfile,
            true,
            handle.connection.latency(),
            handle.gameMode.gameModeForPlayer,
            handle.tabListDisplayName,
            Optionull.map(handle.chatSession, RemoteChatSession::asData)
        )
    }


}