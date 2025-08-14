package com.github.tanokun.bakajinrou.plugin.interaction.participant.dead

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.attacking.BodyHandler
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.dead.body.DisableHittingBody
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
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import java.util.*

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class DeathConfirmedObserver(
    private val game: JinrouGame,
    private val bodyHandler: BodyHandler,
    private val playerProvider: BukkitPlayerProvider,
    private val mainScope: CoroutineScope,
    private val disableHittingBody: DisableHittingBody,
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
        val player = playerProvider.getAllowNull(dead) ?: return

        bodyHandler.createBody(dead.participantId)
        disableHittingBody.ghost(player as CraftPlayer)
        player.inventory.clear()
        player.gameMode = GameMode.SPECTATOR

        val entries = Bukkit.getOnlinePlayers()
            .filterNot { it.uniqueId == dead.participantId.uniqueId }
            .map { player -> createPacketEntry(player as CraftPlayer) }

        if (entries.isEmpty()) return

        player.handle.connection.send(
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