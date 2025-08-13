package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.tab.modifier

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text

class TabListModifier(
    private val game: JinrouGame,
    translator: JinrouTranslator
) {
    val prefixCreator = PrefixCreator(translator)

    fun modifyByUpdateGameMode(viewerUniqueId: ParticipantId, contents: List<PlayerInfoData>): List<PlayerInfoData> {
        val viewer = game.getParticipant(viewerUniqueId) ?: return contents

        return contents.map { entry ->
            val target = game.getParticipant(entry.profileId.asParticipantId()) ?: return@map entry
            if (target.isPosition<SpectatorPosition>()) return@map entry

            val gameType =
                if (isVisibleSpectator(viewer, entry.profileId.asParticipantId())) entry.gameMode else EnumWrappers.NativeGameMode.ADVENTURE

            return@map PlayerInfoData(
                entry.profileId,
                entry.latency,
                entry.isListed,
                gameType,
                entry.profile,
                entry.displayName,
                entry.remoteChatSessionData,
            )
        }
    }

    fun modifyByUpdateDisplayName(viewerPlayer: Player, contents: List<PlayerInfoData>): List<PlayerInfoData>  {
        val viewer = game.getParticipant(viewerPlayer.uniqueId.asParticipantId()) ?: return contents
        val viewerLocale = viewerPlayer.locale()

        return contents.map { entry ->
            val target = game.getParticipant(entry.profileId.asParticipantId()) ?: return@map entry
            if (target.isPosition<SpectatorPosition>()) return@map entry
            val gameType =
                if (isVisibleSpectator(viewer, entry.profileId.asParticipantId())) entry.gameMode else EnumWrappers.NativeGameMode.ADVENTURE


            val displayName = component {
                val prefix = prefixCreator.createPrefix(viewer = viewer, target = target, locale = viewerLocale)

                if (prefix != Component.text("")) {
                    raw { prefix }
                    text(" ")
                }

                text(entry.profile.name)
            }

            return@map PlayerInfoData(
                entry.profileId,
                entry.latency,
                entry.isListed,
                gameType,
                entry.profile,
                WrappedChatComponent.fromHandle(PaperAdventure.asVanilla(displayName)),
                entry.remoteChatSessionData,
            )
        }
    }

    fun initializeDisplayName(targetId: ParticipantId, targetPlayer: Player) {
        val target = game.getParticipant(targetId) ?: return

        if (target.isPosition<WolfPosition>()) {
            targetPlayer.playerListName(targetPlayer.playerListName())
            return
        }

        targetPlayer as CraftPlayer
        val handle = targetPlayer.handle
        val packet = ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, handle)

        handle.connection.sendPacket(packet)
    }

    fun isVisibleSpectator(viewer: Participant, uniqueId: ParticipantId) = viewer.isVisibleSpectators() || uniqueId == viewer.participantId
}