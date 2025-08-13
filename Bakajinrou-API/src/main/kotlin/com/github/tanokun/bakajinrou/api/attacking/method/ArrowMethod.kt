package com.github.tanokun.bakajinrou.api.attacking.method

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import java.util.*

data class ArrowMethod(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason
): AttackMethod() {
    override val assetKey: MethodAssetKeys.Attack = MethodAssetKeys.Attack.ARROW

    override val transportable: Boolean = false

    override fun asTransferred(): GrantedMethod = throw IllegalStateException("この手段は譲渡できません。")
}