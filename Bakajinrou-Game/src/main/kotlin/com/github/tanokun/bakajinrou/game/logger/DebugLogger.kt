package com.github.tanokun.bakajinrou.game.logger

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import java.util.*
import java.util.logging.Logger

class DebugLogger(
    private val logger: Logger,
) {
    private val nameCache = PlayerNameCache

    fun logAttackResult(attacker: UUID, victim: UUID, attackMethod: AttackMethod, attackResult: AttackResult) {
        when (attackResult) {
            is AttackResult.Protected -> logger.info("Attack protected: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim) methods>[attack: $$attackMethod, protective: ${attackResult.by}]")
            AttackResult.SuccessAttack -> logger.info("Attack succeeded: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim) methods>[method: $attackMethod]")
        }
    }

    fun logKill(attacker: UUID, victim: UUID) {
        logger.info("Killed participant: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim)")
    }
}