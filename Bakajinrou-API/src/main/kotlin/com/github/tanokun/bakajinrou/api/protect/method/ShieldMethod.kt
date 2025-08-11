package com.github.tanokun.bakajinrou.api.protect.method

import com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attack.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protect.ActivationPriority
import com.github.tanokun.bakajinrou.api.protect.ProtectResult
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import java.util.*

data class ShieldMethod(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason
): ProtectiveMethod() {
    override val priority: ActivationPriority = ActivationPriority.HIGH

    override val assetKey: MethodAssetKeys.Protective = MethodAssetKeys.Protective.SHIELD

    override fun verifyProtect(method: AttackMethod): ProtectResult = when (method) {
        is ArrowMethod -> ProtectResult.PROTECTED
        else -> ProtectResult.FAILURE
    }

    override fun asTransferred(): GrantedMethod = copy(reason = GrantedReason.TRANSFERRED)
}