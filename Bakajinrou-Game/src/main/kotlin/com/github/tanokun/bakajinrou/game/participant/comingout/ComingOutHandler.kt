package com.github.tanokun.bakajinrou.game.participant.comingout

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.prefix.ComingOut

class ComingOutHandler(private val game: JinrouGame) {
    suspend fun comingOut(participantId: ParticipantId, comingOut: ComingOut?) {
        if (!game.existParticipant(participantId)) return

        game.updateParticipant(participantId) { current ->
            current.copy(comingOut = comingOut)
        }
    }
}