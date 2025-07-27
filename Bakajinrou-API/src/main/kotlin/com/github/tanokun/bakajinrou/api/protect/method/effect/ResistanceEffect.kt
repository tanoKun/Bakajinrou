package com.github.tanokun.bakajinrou.api.protect.method.effect

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.method.ActivationPriority
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod

abstract class ResistanceEffect: ProtectiveMethod {
    override val priority: ActivationPriority = ActivationPriority.NORMAL

    override fun verifyProtect(method: AttackMethod): AttackResult = AttackResult.Protected(this)
}