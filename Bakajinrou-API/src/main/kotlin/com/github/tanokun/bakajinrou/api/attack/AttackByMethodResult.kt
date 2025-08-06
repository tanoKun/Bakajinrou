package com.github.tanokun.bakajinrou.api.attack

import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.participant.Participant

sealed interface AttackByMethodResult {
    val consumeProtectiveMethods: List<ProtectiveMethod>

    data class Protected(override val consumeProtectiveMethods: List<ProtectiveMethod>, val aliveParticipant: Participant): AttackByMethodResult
    data class SucceedAttack(override val consumeProtectiveMethods: List<ProtectiveMethod>, val deadParticipant: Participant): AttackByMethodResult
}