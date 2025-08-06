package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.attack.AttackByMethodResult
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.protect.ProtectResult

abstract class AttackMethod: GrantedMethod {
    /**
     * この攻撃手段を使って、[victim] を攻撃します。
     *
     * @param victim 攻撃対象
     *
     * @return 攻撃結果(防御 -> [AttackByMethodResult.Protected]、成功 -> [AttackByMethodResult.SucceedAttack])
     */
    fun attack(victim: Participant): AttackByMethodResult {

        val consumedProtections = arrayListOf<ProtectiveMethod>()
        for (protectiveMethod in victim.getActiveProtectiveMethods()) {
            consumedProtections.add(protectiveMethod)

            when (protectiveMethod.verifyProtect(method = this)) {
                ProtectResult.PROTECTED -> {
                    val aliveVictim = consumedProtections.fold(victim) { victim, method ->
                        return@fold victim.removeMethod(method)
                    }

                    return AttackByMethodResult.Protected(consumedProtections, aliveVictim)
                }
                ProtectResult.SUCCESS -> continue
            }
        }

        val victim = consumedProtections.fold(victim) { victim, method ->
            return@fold victim.removeMethod(method)
        }

        return AttackByMethodResult.SucceedAttack(consumedProtections, victim.dead())
    }
}