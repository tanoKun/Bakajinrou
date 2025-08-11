package com.github.tanokun.bakajinrou.api.advantage

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import java.util.*

data class ExchangeMethod(
    override val methodId: MethodId = UUID.randomUUID().asMethodId(),
    override val reason: GrantedReason
) : AdvantageMethod() {
    override val assetKey = MethodAssetKeys.Advantage.EXCHANGE

    override fun asTransferred(): GrantedMethod = copy(reason = GrantedReason.TRANSFERRED)
}