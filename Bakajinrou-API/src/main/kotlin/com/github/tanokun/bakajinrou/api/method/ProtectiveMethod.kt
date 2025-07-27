package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.participant.Participant

interface ProtectiveMethod: GrantedMethod {
    val priority: ActivationPriority

    /**
     * [method]での攻撃が、このアイテムで防御可能か検証します。
     *
     * @param method 攻撃手段
     *
     * @return 検証した攻撃の結果
     */
    fun verifyProtect(method: AttackMethod): AttackResult

    /**
     * このアイテムが、防御可能状態にあるか示します。
     * これがfalseだった場合、アイテムは消費されません。
     *
     * @return 防御可能か
     */
    fun isActive(of: Participant): Boolean
}