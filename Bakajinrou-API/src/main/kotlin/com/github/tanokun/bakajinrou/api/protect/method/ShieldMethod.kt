package com.github.tanokun.bakajinrou.api.protect.method

import com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.method.ActivationPriority
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.protect.ProtectResult
import com.github.tanokun.bakajinrou.api.translate.TranslationKey
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys
import java.util.*

data class ShieldMethod(override val uniqueId: UUID = UUID.randomUUID()): ProtectiveMethod() {
    override val priority: ActivationPriority = ActivationPriority.HIGH

    override val assetKey: TranslationKey = TranslationKeys.Method.Protective.SHIELD

    override fun verifyProtect(method: AttackMethod): ProtectResult = when (method) {
        is ArrowMethod -> ProtectResult.PROTECTED
        else -> ProtectResult.SUCCESS
    }
}