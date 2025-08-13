package com.github.tanokun.bakajinrou.api.ability.knight

import com.github.tanokun.bakajinrou.api.ability.ProtectAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protection.ProtectVerificator
import com.github.tanokun.bakajinrou.api.protection.method.TotemMethod
import java.util.*

/**
 * 本物の加護能力
 *
 * [TotemMethod] の防御手段を返します。
 */
data class RealProtectAbility(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason
): ProtectAbility() {

    override fun protect(verificator: ProtectVerificator, methodId: MethodId): TotemMethod =
        TotemMethod(methodId = methodId, reason = GrantedReason.TRANSFERRED, verificator = verificator)


    override fun asCrafted(): GrantedMethod = copy(reason = GrantedReason.CRAFTED)
}