package com.github.tanokun.bakajinrou.game.participant.state.suspended

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId

class ChangeSuspended(private val game: JinrouGame) {
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