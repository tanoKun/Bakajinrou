package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.participant.Participant

interface AttackMethod: GrantedMethod {
    /**
     * この攻撃手段で、[by]が[victim]を攻撃した事を表します。
     *
     * @param by 攻撃者
     * @param victim 攻撃対象
     */
    fun attack(by: Participant, victim: Participant) {
        onConsume(consumer = by)

        for (protectItem in victim.getActiveProtectiveMethods()) {
            protectItem.onConsume(consumer = victim)

            when (protectItem.verifyProtect(method = this)) {
                is AttackResult.Protected -> return
                AttackResult.SuccessAttack -> continue
            }
        }

        onSuccessAttack(by, victim)
    }

    fun onSuccessAttack(by: Participant, victim: Participant)
}