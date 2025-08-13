package com.github.tanokun.bakajinrou.api.attacking

import com.github.tanokun.bakajinrou.api.protect.method.ProtectiveMethod

sealed interface AttackByMethodResult {
    val consumedProtectiveMethods: List<ProtectiveMethod>

    data class Protected(override val consumedProtectiveMethods: List<ProtectiveMethod>): AttackByMethodResult
    data class SucceedAttack(override val consumedProtectiveMethods: List<ProtectiveMethod>): AttackByMethodResult
}