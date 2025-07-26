package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.github.tanokun.bakajinrou.api.participant.Participant
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text
import java.util.*

class TabListModifier(
    private val viewer: Participant,
    private val prefixModifiers: List<PrefixModifier>
) {
    fun modifyContents(contents: List<ClientboundPlayerInfoUpdatePacket.Entry>): List<ClientboundPlayerInfoUpdatePacket.Entry> {
        return contents.mapNotNull { entry ->
            val targetPlayer = Bukkit.getPlayer(entry.profileId) ?: return@mapNotNull null
            val prefixModifier = prefixModifiers.firstOrNull { it.target.uniqueId == entry.profileId } ?: return@mapNotNull entry

            val gameType = if (isVisibleSpectator(entry.profileId)) GameType.byId(targetPlayer.gameMode.value) else GameType.SURVIVAL

            println(entry.profileId == viewer.uniqueId)

            val displayName = component {
                val prefix = prefixModifier.createPrefix(viewer = viewer)

                if (prefix != net.kyori.adventure.text.Component.text("")) {
                    raw { prefix }
                    text(" ")
                }

                text(targetPlayer.name)
            }

            return@mapNotNull ClientboundPlayerInfoUpdatePacket.Entry(
                entry.profileId,
                entry.profile,
                entry.listed,
                entry.latency,
                gameType,
                PaperAdventure.asVanilla(displayName),
                entry.chatSession
            )
        }
    }

    fun isVisibleSpectator(uniqueId: UUID) = viewer.isVisibleSpectators() || uniqueId == viewer.uniqueId
}

fun Player.updatePlayerListName() {
    this.playerListName(this.playerListName())
}