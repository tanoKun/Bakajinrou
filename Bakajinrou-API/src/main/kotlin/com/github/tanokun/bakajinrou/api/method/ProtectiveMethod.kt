package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.participant.Participant

interface ProtectiveMethod: GrantedMethod {
    val priority: ActivationPriority

    /**
     * 指定された攻撃手段による攻撃を、この防御アイテムが防御できるかを判定します。
     *
     * @param method 攻撃手段
     *
     * @return 防御結果
     */
    fun verifyProtect(method: AttackMethod): AttackResult

    /**
     * この防御アイテムが防御可能な状態かを返します。
     * 防御不可能な場合は消費されません。
     *
     * @param of 防御者
     *
     * @return 防御可能であれば true、そうでなければ false
     */
    fun isActive(of: Participant): Boolean
}