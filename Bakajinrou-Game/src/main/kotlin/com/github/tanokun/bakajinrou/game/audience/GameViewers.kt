package com.github.tanokun.bakajinrou.game.audience

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.player.PlayerId
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.game.state.GameStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

sealed interface GameViewer {
    val playerId: PlayerId

    data class Playing(val participant: Participant): GameViewer {
        override val playerId: PlayerId get() = participant.playerId
    }

    data class DeadParticipant(val participant: Participant): GameViewer {
        override val playerId: PlayerId get() = participant.playerId
    }

    data class Spectator(override val playerId: PlayerId): GameViewer
}

data class GameViewerChange(
    val playerId: PlayerId,
    val before: GameViewer?,
    val after: GameViewer?,
)

/** Domain の参加者と外部観戦者から、UI が扱う閲覧者を投影します。 */
class GameViewers(
    private val game: GameStore,
    private val audience: GameAudience,
    gameChanges: GameChanges,
) {
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

    fun resolve(playerId: PlayerId): GameViewer? {
        val participant = game.getCurrentParticipants().find { it.playerId == playerId }
        if (participant != null) return participantViewer(participant)
        if (audience.isSpectator(playerId)) return GameViewer.Spectator(playerId)

        return null
    }

    fun allPlayerIds(): Set<PlayerId> =
        game.getCurrentParticipants().mapTo(mutableSetOf()) { it.playerId } + audience.current.spectators

    fun observerPlayerIds(): Set<PlayerId> =
        game.getCurrentParticipants()
            .filter(Participant::isDead)
            .mapTo(mutableSetOf()) { it.playerId } + audience.current.spectators

    private fun participantViewer(participant: Participant): GameViewer =
        if (participant.isDead()) GameViewer.DeadParticipant(participant)
        else GameViewer.Playing(participant)
}
