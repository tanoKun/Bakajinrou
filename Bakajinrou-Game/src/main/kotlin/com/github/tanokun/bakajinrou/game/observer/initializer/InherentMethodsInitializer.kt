package com.github.tanokun.bakajinrou.game.observer.initializer

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController

class InherentMethodsInitializer(
    private val jinrouGame: JinrouGame, gameController: JinrouGameController
) : ParticipantInitializer(jinrouGame, gameController, { true }) {
    override fun initialize(self: Participant, participants: ParticipantScope) {
        val methods = self.position.inherentMethods(jinrouGame)

        val self = methods.fold(self) { self, method ->
            return@fold self.grantMethod(method)
        }

        jinrouGame.updateParticipant(self)
    }
}