package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.Translator
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text
import java.util.*

class TabListModifier(
    private val game: JinrouGame,
    translator: Translator
) {
    val prefixCreator = PrefixCreator(translator)

    fun modifyByUpdateGameMode(viewerUniqueId: UUID, contents: List<PlayerInfoData>): List<PlayerInfoData> {
        val viewer = game.getParticipant(viewerUniqueId) ?: return contents

        return contents.map { entry ->
            val gameType = if (isVisibleSpectator(viewer, entry.profileId)) entry.gameMode else EnumWrappers.NativeGameMode.SURVIVAL

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
        val viewer = game.getParticipant(viewerPlayer.uniqueId) ?: return contents
        val viewerLocale = viewerPlayer.locale()

        return contents.map { entry ->
            val gameType = if (isVisibleSpectator(viewer, entry.profileId)) entry.gameMode else EnumWrappers.NativeGameMode.SURVIVAL
            val target = game.getParticipant(entry.profileId) ?: return@map entry

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


    fun isVisibleSpectator(viewer: Participant, uniqueId: UUID) = viewer.isVisibleSpectators() || uniqueId == viewer.uniqueId
}

fun Player.updatePlayerListName() {
    this.playerListName(this.playerListName())
}