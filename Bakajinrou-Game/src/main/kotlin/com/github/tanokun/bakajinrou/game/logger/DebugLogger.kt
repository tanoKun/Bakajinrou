package com.github.tanokun.bakajinrou.game.logger

import com.github.tanokun.bakajinrou.api.attacking.method.AttackMethod
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.game.attacking.AttackResolution
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import java.util.logging.Logger

class DebugLogger(
    private val logger: Logger,
) {
    private val nameCache = PlayerNameCache

    fun logAttacked(attackMethod: AttackMethod, attackResolution: AttackResolution) {
        val attacker = attackResolution.attackerId.uniqueId
        val victim = attackResolution.victimId.uniqueId

        when (attackResolution) {
            is AttackResolution.Alive -> logger.info("Attack protected: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim) methods>[attack: $attackMethod, protective: ${attackResolution.result.consumedProtectiveMethods}]")
            is AttackResolution.Killed -> logger.info("Attack succeeded: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim) methods>[method: $attackMethod]")
        }
    }

    fun logKill(attacker: ParticipantId, victim: ParticipantId) {
        val attacker = attacker.uniqueId
        val victim = victim.uniqueId

        logger.info("Killed participant: ${nameCache.get(attacker)}($attacker) -> ${nameCache.get(victim)}($victim)")
    }

    fun logAddMethod(target: ParticipantId, add: MethodDifference.Granted) {
        val target = target.uniqueId

        logger.info("Add method: ${nameCache.get(target)}($target) got ${add.grantedMethod}")
    }

    fun logRemoveMethod(target: ParticipantId, add: MethodDifference.Removed) {
        val target = target.uniqueId

        logger.info("Remove method: ${nameCache.get(target)}($target) lost ${add.removedMethod}")
    }
}