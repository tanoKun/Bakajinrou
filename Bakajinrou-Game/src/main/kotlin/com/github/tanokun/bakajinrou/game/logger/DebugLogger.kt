package com.github.tanokun.bakajinrou.game.logger

import com.github.tanokun.bakajinrou.api.attack.AttackByMethodResult
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import java.util.*
import java.util.logging.Logger

class DebugLogger(
    private val logger: Logger,
) {
    private val nameCache = PlayerNameCache

    fun logAttacked(attacker: UUID, victim: UUID, attackMethod: AttackMethod, attackByMethodResult: AttackByMethodResult) {
        when (attackByMethodResult) {
            is AttackByMethodResult.Protected -> logger.info("Attack protected: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim) methods>[attack: $$attackMethod, protective: ${attackByMethodResult.consumeProtectiveMethods}]")
            is AttackByMethodResult.SucceedAttack -> logger.info("Attack succeeded: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim) methods>[method: $attackMethod]")
        }
    }

    fun logKill(attacker: UUID, victim: UUID) {
        logger.info("Killed participant: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim)")
    }
}