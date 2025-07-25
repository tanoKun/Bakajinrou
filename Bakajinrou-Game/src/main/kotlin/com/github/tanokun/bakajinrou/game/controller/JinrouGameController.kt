package com.github.tanokun.bakajinrou.game.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.attack.AttackVerifier
import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import com.github.tanokun.bakajinrou.game.logger.GameActionLogger
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import java.util.*

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

    fun killed(victim: UUID, by: UUID) {
        val victim = game.getParticipant(victim) ?: return
        val attacker = game.getParticipant(by) ?: return

        if (victim.state != ParticipantStates.SURVIVED) return

        victim.dead()

        logger.logKillParticipantToSpectator(victim.uniqueId, attacker.uniqueId)
        bodyHandler.createBody(victim.uniqueId)

        game.judge()?.let { finisher ->
            finish(finisher)
        }
    }

    fun onAttack(by: AttackVerifier, to: UUID, onAttack: (AttackResult) -> Unit) {
        val victim = game.getParticipant(to) ?: return

        val attackResult =
            if (victim.state != ParticipantStates.SURVIVED)
                AttackResult.INVALID_ATTACK
            else
                by.verify(to = victim)

        onAttack(attackResult)
    }

    fun finish(finisher: GameFinisher) {
        finisher.notifyFinish()
        scheduler.cancel()
    }

    fun launch() {
        scheduler.launch()
        game.participants.forEach { it.position.doAtStarting(it.uniqueId) }
    }
}