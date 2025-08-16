package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.Optionull
import net.minecraft.network.chat.RemoteChatSession
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.world.level.GameType
import org.bukkit.craftbukkit.entity.CraftPlayer
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text
import java.util.*

/**
 */
class TabListModifier(
    private val game: JinrouGame,
    private val playerProvider: BukkitPlayerProvider,
    translator: JinrouTranslator
) {
    val prefixCreator = PrefixCreator(translator)

    fun updateDisplayNameToAll(targetId: ParticipantId) {
        val target = game.getParticipant(targetId) ?: return

        game.getCurrentParticipants()
            .mapNotNull { playerProvider.getAllowNull(it) as? CraftPlayer }
            .forEach { viewerPlayer -> updateDisplayNameOf(listOf(target), viewerPlayer) }
    }

    fun updateDisplayNameToSelf(targetId: ParticipantId) {
        val target = game.getParticipant(targetId) ?: return
        val targetPlayer = playerProvider.getAllowNull(target) as? CraftPlayer ?: return

        updateDisplayNameOf(listOf(target), targetPlayer)
    }

    fun updateDisplayNameOfAll(viewerId: ParticipantId) {
        val viewerPlayer = playerProvider.getAllowNull(viewerId) as? CraftPlayer ?: return

        updateDisplayNameOf(game.getCurrentParticipants(), viewerPlayer)
    }

    fun updateGameModeOfAll(viewerId: ParticipantId) {
        val viewerPlayer = playerProvider.getAllowNull(viewerId) as? CraftPlayer ?: return

        val targetIds = game.getCurrentParticipants()
            .filter(Participant::isDead)
            .map(Participant::participantId)

        updateGameModeOf(targetIds, viewerPlayer)
    }

    private fun updateDisplayNameOf(targets: Collection<Participant>, viewerPlayer: CraftPlayer) {
        val viewer = game.getParticipant(viewerPlayer.uniqueId.asParticipantId()) ?: return

        val entries = targets.map { target ->
            val targetPlayer = playerProvider.getAllowNull(target) as? CraftPlayer ?: return
            val displayName = createDisplayName(viewer, target, PlayerNameCache.get(target) ?: "", viewerPlayer.locale())
            val gameMode = viewerPlayer.handle.gameMode.gameModeForPlayer

            createEntry(targetPlayer.handle, displayName, gameMode)
        }

        val packet = ClientboundPlayerInfoUpdatePacket(
            EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME),
            entries
        )

        viewerPlayer.handle.connection.sendPacket(packet)
    }

    private fun updateGameModeOf(targetIds: List<ParticipantId>, viewerPlayer: CraftPlayer) {
        val viewer = game.getParticipant(viewerPlayer.uniqueId.asParticipantId()) ?: return

        val entries = targetIds.map { targetId ->
            val originGameMode = viewerPlayer.handle.gameMode.gameModeForPlayer

            val viewGameMode =
                if (originGameMode == GameType.SPECTATOR && !isVisibleSpectator(viewer, targetId))
                    GameType.ADVENTURE
                else originGameMode

            createEntry(viewerPlayer.handle, Component.text(""), viewGameMode)
        }

        val packet = ClientboundPlayerInfoUpdatePacket(
            EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE),
            entries
        )

        viewerPlayer.handle.connection.sendPacket(packet)
    }

    private fun createDisplayName(viewer: Participant, target: Participant, name: String, locale: Locale): Component =
        component {
            val prefix = prefixCreator.createPrefix(viewer = viewer, target = target, locale = locale)

            if (prefix != Component.text("")) {
                raw { prefix }
                text(" ")
            }

            text(name)
        }

    private fun createEntry(handle: ServerPlayer, displayName: Component, gameMode: GameType): ClientboundPlayerInfoUpdatePacket.Entry {
        return ClientboundPlayerInfoUpdatePacket.Entry(
            handle.uuid,
            handle.gameProfile,
            true,
            handle.connection.latency(),
            gameMode,
            PaperAdventure.asVanilla(displayName),
            handle.isModelPartShown(PlayerModelPart.HAT),
            handle.tabListOrder,
            Optionull.map(handle.chatSession, RemoteChatSession::asData)
        )
    }

    fun initializeDisplayNameOfAll(targetId: ParticipantId) {
        val target = game.getParticipant(targetId) ?: return

        if (target.isPosition<WolfPosition>()) {
            updateDisplayNameToAll(targetId)
            return
        }

        updateDisplayNameToSelf(targetId)
    }

    private fun isVisibleSpectator(viewer: Participant, targetId: ParticipantId) =
        viewer.isVisibleSpectators() || targetId == viewer.participantId
}