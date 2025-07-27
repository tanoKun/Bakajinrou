package com.github.tanokun.bakajinrou.api.protect.method.item

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.api.method.ActivationPriority
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod

abstract class ShieldItem: ProtectiveMethod {
    override val priority: ActivationPriority = ActivationPriority.HIGH

    override fun verifyProtect(method: AttackMethod): AttackResult = when (method) {
        is ArrowMethod -> AttackResult.Protected(this)
        else -> AttackResult.SuccessAttack
    }
}