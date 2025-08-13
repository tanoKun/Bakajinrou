package com.github.tanokun.bakajinrou.api.attacking.method

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import java.util.*

data class DamagePotionMethod(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason
): AttackMethod() {
    override val assetKey: MethodAssetKeys.Attack = MethodAssetKeys.Attack.DAMAGE_POTION

    override fun asTransferred(): GrantedMethod = copy(reason = GrantedReason.TRANSFERRED)
}