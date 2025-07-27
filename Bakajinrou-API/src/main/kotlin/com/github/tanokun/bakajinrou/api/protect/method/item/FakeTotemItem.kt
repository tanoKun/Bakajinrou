package com.github.tanokun.bakajinrou.api.protect.method.item

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.method.ActivationPriority
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod

abstract class FakeTotemItem: ProtectiveMethod {
    override val priority: ActivationPriority = ActivationPriority.LOW

    override fun verifyProtect(method: AttackMethod): AttackResult = AttackResult.SuccessAttack
}