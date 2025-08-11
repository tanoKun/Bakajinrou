package com.github.tanokun.bakajinrou.api.participant.strategy

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.ParticipantId

interface MethodDifference {
    val participantId: ParticipantId

    data class Granted(override val participantId: ParticipantId, val grantedMethod: GrantedMethod): MethodDifference
    data class Removed(override val participantId: ParticipantId, val removedMethod: GrantedMethod): MethodDifference
}