package com.github.tanokun.bakajinrou.api.attack

import com.github.tanokun.bakajinrou.api.protect.method.ProtectiveMethod

sealed interface AttackByMethodResult {
    val consumeProtectiveMethods: List<ProtectiveMethod>

    data class Protected(override val consumeProtectiveMethods: List<ProtectiveMethod>): AttackByMethodResult
    data class SucceedAttack(override val consumeProtectiveMethods: List<ProtectiveMethod>): AttackByMethodResult
}