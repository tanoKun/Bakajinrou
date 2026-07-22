package com.github.tanokun.bakajinrou.game.viewer

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.player.PlayerId
import com.github.tanokun.bakajinrou.game.audience.AudienceChange
import com.github.tanokun.bakajinrou.game.audience.GameAudience
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.game.state.GameStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

/** Domain の参加者と外部観戦者から、現在の閲覧形態を投影します。 */
class GameViewers(
    private val game: GameStore,
    private val audience: GameAudience,
    gameChanges: GameChanges,
) {
    val current: Set<GameViewer>
        get() = game.getCurrentParticipants()
            .mapTo(mutableSetOf(), ::participantViewer) +
            audience.current.spectators.map(GameViewer::Spectator)

    val spectating: Set<GameViewer>
        get() = current.filterTo(mutableSetOf()) { it !is GameViewer.Playing }

    val changes: Flow<GameViewerChange> = merge(
        audience.changes.transform { change ->
            when (change) {
                is AudienceChange.Joined -> emit(
                    GameViewerChange(change.playerId, null, GameViewer.Spectator(change.playerId))
                )
                is AudienceChange.Left -> emit(
                    GameViewerChange(change.playerId, GameViewer.Spectator(change.playerId), null)
                )
            }
        },
        gameChanges.participantChanges.transform { difference ->
            val before = difference.before?.let(::participantViewer)
            val after = participantViewer(difference.after)
            if (before?.javaClass != after.javaClass) {
                emit(GameViewerChange(after.playerId, before, after))
            }
        },
    )

    fun find(playerId: PlayerId): GameViewer? {
        val participant = game.getCurrentParticipants().find { it.playerId == playerId }
        if (participant != null) return participantViewer(participant)
        if (audience.isSpectator(playerId)) return GameViewer.Spectator(playerId)
        return null
    }

    private fun participantViewer(participant: Participant): GameViewer =
        if (participant.isDead()) GameViewer.DeadParticipant(participant)
        else GameViewer.Playing(participant)
}
