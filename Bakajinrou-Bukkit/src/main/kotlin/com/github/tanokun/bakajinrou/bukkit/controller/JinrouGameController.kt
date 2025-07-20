package com.github.tanokun.bakajinrou.bukkit.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.finishing.GameFinishDecider
import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.bukkit.logger.BodyHandler
import com.github.tanokun.bakajinrou.bukkit.logger.GameActionLogger
import java.util.*

class JinrouGameController(
    private val game: JinrouGame,
    private val finishDecider: GameFinishDecider,
    private val logger: GameActionLogger,
    private val scheduler: GameScheduler,
    private val bodyHandler: BodyHandler
) {
    init {
        require(finishDecider.decide(game.participants) == null) {
            "始めるにあたって、不十分な役職配布です。"
        }
    }

    fun killed(victim: UUID, by: UUID) {
        game.changeToDead(victim)

        logger.logKillParticipantToSpectator(victim, by)
        bodyHandler.createBody(victim)

        finishDecider.decide(game.participants)?.let { finisher ->
            finish(finisher)
        }
    }

    fun finish(finisher: GameFinisher) {
        finisher.notifyFinish()
        scheduler.cancel()
    }
}