package com.github.tanokun.bakajinrou.game.viewer

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.player.PlayerId
import com.github.tanokun.bakajinrou.game.audience.GameAudience

/** ゲームと観戦者のスナップショットから、閲覧形態を導出する helper です。 */
class GameViewers(
    private val game: JinrouGame,
    private val audience: GameAudience,
) {
    val all: Set<GameViewer> = game.participants
        .mapTo(mutableSetOf(), ::participantViewer) +
        audience.spectators.map(GameViewer::Spectator)

    val spectating: Set<GameViewer> =
        all.filterTo(mutableSetOf()) { it !is GameViewer.Playing }

    fun find(playerId: PlayerId): GameViewer? {
        val participant = game.participants.find { it.playerId == playerId }
        if (participant != null) return participantViewer(participant)
        if (playerId in audience.spectators) return GameViewer.Spectator(playerId)

        return null
    }

    private fun participantViewer(participant: Participant): GameViewer =
        if (participant.isDead()) GameViewer.DeadParticipant(participant)
        else GameViewer.Playing(participant)
}
