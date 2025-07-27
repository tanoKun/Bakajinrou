package com.github.tanokun.bakajinrou.api.attack

import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod

sealed interface AttackResult {
    data class Protected(val by: ProtectiveMethod): AttackResult
    data object SuccessAttack: AttackResult
}