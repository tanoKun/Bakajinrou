package com.github.tanokun.bakajinrou.game.controller

import com.github.tanokun.bakajinrou.game.controller.GameFinishHandler
import com.github.tanokun.bakajinrou.game.JinrouGame
import com.github.tanokun.bakajinrou.game.logging.ActionLogger
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import java.util.UUID

class JinrouGameController(
    private val game: JinrouGame,
    private val finishHandler: GameFinishHandler,
    private val logger: ActionLogger,
    private val scheduler: GameScheduler
) {
    init {
        require(finishHandler.getWinners(game.participants).isNotEmpty()) {
            "始めるにあたって、不十分な役職配布です。"
        }
    }

    fun killed(victim: UUID, by: UUID) {
        game.changeToDead(victim)

        logger.logKillParticipant(victim, by)
        logger.createBody(victim)

        val winners = finishHandler.getWinners(game.participants)
        if (winners.isNotEmpty()) {
            finishHandler.finish(winners, game.participants)
        }
    }
}