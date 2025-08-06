package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.protect.ProtectResult

abstract class ProtectiveMethod: GrantedMethod {
    abstract val priority: ActivationPriority

    var isActive: Boolean = false
        internal set

    /**
     * 指定された攻撃手段による攻撃を、この防御アイテムが防御できるかを判定します。
     *
     * @param method 攻撃手段
     *
     * @return 防御結果
     */
    abstract fun verifyProtect(method: AttackMethod): ProtectResult
}