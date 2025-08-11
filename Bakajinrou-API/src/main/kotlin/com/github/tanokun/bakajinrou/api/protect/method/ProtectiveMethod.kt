package com.github.tanokun.bakajinrou.api.protect.method

import com.github.tanokun.bakajinrou.api.attack.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.protect.ActivationPriority
import com.github.tanokun.bakajinrou.api.protect.ProtectResult
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

abstract class ProtectiveMethod: GrantedMethod {
    abstract override val assetKey: MethodAssetKeys.Protective

    abstract val priority: ActivationPriority

    var isActive: Boolean = false
        internal set

    override val transportable: Boolean = true

    /**
     * 指定された攻撃と、その時点でのプレイヤーの状態（コンテキスト）を基に、
     * この防御手段が有効かを判定します。
     *
     * @param method 攻撃手段
     * @param context 防御を実行する瞬間のプレイヤーの状態
     * @return 防御結果
     */
    abstract fun verifyProtect(method: AttackMethod): ProtectResult
}