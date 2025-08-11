package com.github.tanokun.bakajinrou.game.participant.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession

class InherentMethodsInitializer(
    private val game: JinrouGame, gameController: JinrouGameSession
) : ParticipantInitializer(game, gameController, { true }) {
    override suspend fun initialize(selfId: ParticipantId, participants: ParticipantScope) {
        val self = game.getParticipant(selfId) ?: return

        val methods = self.position.inherentMethods()

        game.updateParticipant(selfId) { current ->
            methods.fold(current) { self, method -> self.grantMethod(method) }
        }
    }
}