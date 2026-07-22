package com.github.tanokun.bakajinrou.game.participant.state.suspended

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.participant.ParticipantId

class ChangeSuspended(private val game: GameStore) {
    suspend fun changeToAlive(targetId: ParticipantId) {
        val current = game.getParticipant(targetId) ?: return
        if (!current.isSuspended()) return

        game.updateParticipant(targetId) { current ->
            current.alive()
        }
    }

    suspend fun changeToSuspended(targetId: ParticipantId) {
        val current = game.getParticipant(targetId) ?: return
        if (!current.isAlive()) return

        game.updateParticipant(targetId) { current ->
            current.suspended()
        }
    }
}
