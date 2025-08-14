package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.wrappers.EnumWrappers
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleListener
import com.github.tanokun.bakajinrou.plugin.common.listener.packet.LifecyclePacketListener
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier.TabListModifier
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.world.level.GameType
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [LifecycleListener::class])
@Scope(value = GameComponents::class)
class TabListHandler(
    plugin: Plugin,
    game: JinrouGame,
    protocolManager: ProtocolManager,
    tabModifier: TabListModifier,
    playerProvider: BukkitPlayerProvider
) : LifecyclePacketListener(plugin, protocolManager, {
    val suspendedPlayers = mutableSetOf<ParticipantId>()

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!packet.playerInfoActions.read(0).contains(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE)) return@register

        val copy = packet.deepClone().apply {
            playerInfoDataLists.write(1, tabModifier.modifyByUpdateGameMode(receiver.uniqueId.asParticipantId(), packet.playerInfoDataLists.read(1)))
        }

        event.packet = copy
    }

    register(packet = PacketType.Play.Server.PLAYER_INFO, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!packet.playerInfoActions.read(0).contains(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME)) return@register

        val copy = packet.deepClone().apply {
            playerInfoDataLists.write(1, tabModifier.modifyByUpdateDisplayName(receiver, packet.playerInfoDataLists.read(1)))
        }

        event.packet = copy
    }

    register(packet = PacketType.Play.Server.PLAYER_INFO_REMOVE, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        if (!game.existParticipant(receiver.uniqueId.asParticipantId())) return@register

        val players = packet.uuidLists.read(0).mapNotNull { game.getParticipant(it.asParticipantId()) }
        suspendedPlayers.addAll(players.map(Participant::participantId))

        if (players.isNotEmpty()) event.isCancelled = true
    }

    register(packet = PacketType.Play.Server.ENTITY_DESTROY, listenerPriority = ListenerPriority.LOW) { event, packet, receiver ->
        val players = (event.player.world as CraftWorld).handle.players()
        val playerIds = players.map { it.id }

        val ints = packet.intLists.read(0)
        val diff = ints
            .filter { playerIds.contains(it) }
            .filter { playerId ->
                val player = players.first { it.id == playerId }
                player.gameMode.gameModeForPlayer == GameType.SPECTATOR && player.gameMode.previousGameModeForPlayer != GameType.SPECTATOR
            }

        event.packet = packet.deepClone().apply {
            intLists.write(0, ints - diff)
        }
    }

    onCancellation {
        val packet = ClientboundPlayerInfoRemovePacket(
            suspendedPlayers
                .filter { playerProvider.getAllowNull(it) == null }
                .map { it.uniqueId }
        )

        game.getCurrentParticipants().forEach {
            val player = (playerProvider.getAllowNull(it) as? CraftPlayer) ?: return@forEach
            player.handle.connection.send(packet)
        }
    }
})