package com.github.tanokun.bakajinrou.plugin.interaction.participant.rendering.team.modifier

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.isMadman
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import net.minecraft.world.scores.Team


class ViewTeamModifier(
    private val game: JinrouGame,
    private val playerProvider: BukkitPlayerProvider,
    participants: ParticipantScope.NonSpectators,
) {
    private val hiddenTeam: TeamPackets =
        TeamPackets("all_always_hidden", participants.mapNotNull { PlayerNameCache.get(it) }) {
            nameTagVisibility = Team.Visibility.NEVER
            setSeeFriendlyInvisibles(false)
        }

    private val wolfVisibleTeam: TeamPackets

    private val madmanVisibleTeams: Map<ParticipantId, TeamPackets>

    init {

        val wolfs = participants.includes(::isWolf)
        wolfVisibleTeam = TeamPackets("wolfs", wolfs.mapNotNull { PlayerNameCache.get(it) }) {
            nameTagVisibility = Team.Visibility.ALWAYS
        }

        val madmans = participants.includes(::isMadman).map { madman ->
            val knownWolf = wolfs.first { (it.position as WolfPosition).knownByMadmans.contains(madman.participantId) }

            madman.participantId to knownWolf
        }

        madmanVisibleTeams = madmans.mapNotNull { (madman, wolf) ->
            val wolfName = PlayerNameCache.get(wolf) ?: return@mapNotNull null
            val madmanName = PlayerNameCache.get(madman) ?: return@mapNotNull null

            madman to TeamPackets("madman", listOf(wolfName, madmanName)) {
                nameTagVisibility = Team.Visibility.ALWAYS
            }
        }.associate { it }
    }

    fun applyModification(viewerId: ParticipantId) {
        val viewer = game.getParticipant(viewerId) ?: return
        val viewerPlayer = playerProvider.getAllowNull(viewer) ?: return

        hiddenTeam.sendPackets(viewerPlayer)
        if (viewer.isPosition<WolfPosition>()) {
            wolfVisibleTeam.sendPackets(viewerPlayer)
            return
        }

        if (viewer.isPosition<MadmanPosition>()) {
            val team = madmanVisibleTeams[viewerId] ?: return
            team.sendPackets(viewerPlayer)
        }
    }
}