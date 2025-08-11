package com.github.tanokun.bakajinrou.game.participant.observer.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession

class InherentMethodsInitializer(
    private val jinrouGame: JinrouGame, gameController: JinrouGameSession
) : ParticipantInitializer(jinrouGame, gameController, { true }) {
    override suspend fun initialize(selfId: ParticipantId, participants: ParticipantScope) {
        val self = jinrouGame.getParticipant(selfId) ?: return

        val methods = self.position.inherentMethods()

        jinrouGame.updateParticipant(selfId) { current ->
            methods.fold(current) { self, method -> self.grantMethod(method) }
        }
    }
}