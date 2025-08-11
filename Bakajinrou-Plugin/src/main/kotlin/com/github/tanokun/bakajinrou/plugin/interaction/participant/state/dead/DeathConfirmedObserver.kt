package com.github.tanokun.bakajinrou.plugin.interaction.participant.state.dead

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.attack.BodyHandler
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.minecraft.Optionull
import net.minecraft.network.chat.RemoteChatSession
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.craftbukkit.entity.CraftPlayer
import java.util.*

class DeathConfirmedObserver(
    private val game: JinrouGame,
    private val bodyHandler: BodyHandler,
    private val playerProvider: BukkitPlayerProvider,
    mainScope: CoroutineScope,
): Observer {
    init {
        mainScope.launch {
            game.observeParticipants(mainScope)
                .filter { it.after.isDead() }
                .map { it.after }
                .collect(::onDeath)
        }
    }

    private fun onDeath(dead: Participant) {
        bodyHandler.showBodies(dead.participantId)

        val player = playerProvider.getAllowNull(dead) ?: return

        player.gameMode = GameMode.SPECTATOR

        val entries = Bukkit.getOnlinePlayers()
            .filterNot { it.uniqueId == dead.participantId.uniqueId }
            .map { player -> createPacketEntry(player as CraftPlayer) }

        if (entries.isEmpty()) return

        (player as CraftPlayer).handle.connection.send(
            ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(
                    ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                    ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE
                ), entries
            )
        )
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