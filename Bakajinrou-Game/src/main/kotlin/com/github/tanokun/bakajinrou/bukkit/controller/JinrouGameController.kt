package com.github.tanokun.bakajinrou.bukkit.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.attack.AttackVerifier
import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.bukkit.logger.BodyHandler
import com.github.tanokun.bakajinrou.bukkit.logger.GameActionLogger
import com.github.tanokun.bakajinrou.bukkit.scheduler.GameScheduler
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

        victim.dead()

        logger.logKillParticipantToSpectator(victim.uniqueId, attacker.uniqueId)
        bodyHandler.createBody(victim.uniqueId)

        game.judge()?.let { finisher ->
            finish(finisher)
        }
    }

    fun onAttack(by: AttackVerifier, to: UUID, vararg onAttacks: Pair<AttackResult, () -> Unit>) {
        val attacker = game.getParticipant(to) ?: return

        val attackResult = by.verify(to = attacker)

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