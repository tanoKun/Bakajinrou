package com.github.tanokun.bakajinrou.bukkit.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.attack.AttackVerifier
import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.finishing.JinrouGameFinishDecider
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.logger.BodyHandler
import com.github.tanokun.bakajinrou.bukkit.logger.GameActionLogger
import com.github.tanokun.bakajinrou.bukkit.scheduler.GameScheduler
import java.util.*

class JinrouGameController(
    private val game: JinrouGame,
    private val finishDecider: JinrouGameFinishDecider,
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

    fun onAttack(by: AttackVerifier, toPlayer: Participant, vararg onAttacks: Pair<AttackResult, () -> Unit>) {
        val attackResult = by.verify(to = toPlayer)

        onAttacks
            .filter { it.first == attackResult }
            .forEach {
                it.second.invoke()
            }
    }


    fun finish(finisher: GameFinisher) {
        finisher.notifyFinish()
        scheduler.cancel()
    }
}