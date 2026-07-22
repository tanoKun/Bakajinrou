package com.github.tanokun.bakajinrou.game.viewer

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.player.PlayerId

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
