package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.participant.Participant

abstract class AttackMethod: GrantedMethod {
    /**
     * この攻撃手段を使って、[victim] を攻撃します。
     * [victim] が複数の防御手段を所有している場合、防御が成功になるまで消費し続けます。
     * データ上の攻撃を行い、UI上の変化はありません。[AttackResult] によって変える必要があります。
     *
     * 副作用:
     * - 攻撃が成功した際、 [victim] を死亡状態に
     * - [victim] の持つ防御手段の消費と剥奪
     *
     * @param victim 攻撃対象
     *
     * @return 攻撃結果(防御 -> [AttackResult.Protected]、成功 -> [AttackResult.SuccessAttack])
     */
    fun attack(victim: Participant): AttackResult {
        for (protectiveMethod in victim.getActiveProtectiveMethods()) {
            protectiveMethod.onConsume(consumer = victim)
            victim.removeMethod(protectiveMethod)

            when (val result = protectiveMethod.verifyProtect(method = this)) {
                is AttackResult.Protected -> return result
                AttackResult.SuccessAttack -> continue
            }
        }

        victim.dead()

        return AttackResult.SuccessAttack
    }
}