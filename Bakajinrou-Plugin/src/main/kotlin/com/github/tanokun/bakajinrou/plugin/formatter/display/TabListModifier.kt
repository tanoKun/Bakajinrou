package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.github.tanokun.bakajinrou.api.participant.Participant
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text
import java.util.*

class TabListModifier(
    private val viewer: Participant,
    private val prefixModifiers: List<PrefixModifier>
) {

    fun modifyByUpdateGameMode(contents: List<PlayerInfoData>): List<PlayerInfoData>  {
        return contents.map { entry ->
            if (prefixModifiers.firstOrNull { it.target.uniqueId == entry.profileId } == null) return@map entry

            val gameType = if (isVisibleSpectator(entry.profileId)) entry.gameMode else EnumWrappers.NativeGameMode.SURVIVAL

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

    fun modifyByUpdateDisplayName(contents: List<PlayerInfoData>): List<PlayerInfoData>  {
        return contents.map { entry ->
            val gameType = if (isVisibleSpectator(entry.profileId)) entry.gameMode else EnumWrappers.NativeGameMode.SURVIVAL
            val prefixModifier = prefixModifiers.firstOrNull { it.target.uniqueId == entry.profileId } ?: return@map entry

            val displayName = component {
                val prefix = prefixModifier.createPrefix(viewer = viewer)

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


    fun isVisibleSpectator(uniqueId: UUID) = viewer.isVisibleSpectators() || uniqueId == viewer.uniqueId
}

fun Player.updatePlayerListName() {
    this.playerListName(this.playerListName())
}