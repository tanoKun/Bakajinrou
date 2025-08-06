package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.kyori.adventure.translation.Translator
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.scores.Team.Visibility
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class NameTagModifier(
    private val viewer: Participant,
    translator: Translator
) {
    val prefixCreator = PrefixCreator(translator)

    fun modifyNameTag(target: Participant) {
        val viewerPlayer = BukkitPlayerProvider.get(viewer) ?: return
        val targetPlayer = BukkitPlayerProvider.get(target) ?: return
        val prefixComponent = prefixCreator.createPrefix(viewer, target, viewerPlayer.locale())

        if (prefixComponent == Component.text("")) {
            modifyNameTag(viewerPlayer, targetPlayer) {
                nameTagVisibility = Visibility.NEVER
            }
            return
        }

        modifyNameTag(viewerPlayer, targetPlayer) {
            playerPrefix = PaperAdventure.asVanilla(prefixComponent)
        }
    }

    private fun modifyNameTag(viewer: Player, target: Player, dsl: PlayerTeam.() -> Unit) {
        target as CraftPlayer
        viewer as CraftPlayer

        val dummyTeam = PlayerTeam(Scoreboard(), "team_${target.name}").apply(dsl)

        val createTeamPacket = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(dummyTeam, true)
        viewer.handle.connection.send(createTeamPacket)

        val joinTeamPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(
            dummyTeam,
            target.handle.gameProfile.name,
            ClientboundSetPlayerTeamPacket.Action.ADD
        )
        viewer.handle.connection.send(joinTeamPacket)
    }
}