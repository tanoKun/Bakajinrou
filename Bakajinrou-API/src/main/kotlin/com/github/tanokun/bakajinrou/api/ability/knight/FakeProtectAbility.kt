package com.github.tanokun.bakajinrou.api.ability.knight

import com.github.tanokun.bakajinrou.api.ability.ProtectAbility
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protect.ProtectVerificator
import com.github.tanokun.bakajinrou.api.protect.method.FakeTotemMethod
import java.util.*

/**
 * 偽物の加護能力
 *
 * [FakeTotemMethod] の防御手段を返します。
 */
data class FakeProtectAbility(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason
): ProtectAbility() {

    override fun protect(verificator: ProtectVerificator): FakeTotemMethod =
        FakeTotemMethod(reason = GrantedReason.TRANSFERRED, verificator = verificator)
}