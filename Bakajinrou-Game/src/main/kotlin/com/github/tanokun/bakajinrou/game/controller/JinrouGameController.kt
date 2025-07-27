package com.github.tanokun.bakajinrou.game.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import com.github.tanokun.bakajinrou.game.logger.GameActionLogger
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler

class JinrouGameController(
    private val game: JinrouGame,
    private val logger: GameActionLogger,
    private val scheduler: GameScheduler,
    private val bodyHandler: BodyHandler
) {
    init {
        require(game.judge() == null) {
            "始めるにあたって、不十分な役職配布です。"
        }
    }

    fun killed(victim: Participant, by: Participant) {
        if (!game.participants.contains(victim)) return
        if (!game.participants.contains(by)) return

        if (victim.state != ParticipantStates.SURVIVED) return

        victim.dead()

        logger.logKillParticipantToSpectator(victim.uniqueId, by.uniqueId)
        bodyHandler.createBody(victim.uniqueId)

        game.judge()?.let { finisher ->
            finish(finisher)
        }
    }

    fun finish(finisher: GameFinisher) {
        finisher.notifyFinish()

        if (scheduler.isActive()) scheduler.cancel()
    }

    fun launch() {
        scheduler.launch()
        game.participants.forEach { it.position.doAtStarting(it.uniqueId) }
    }
}