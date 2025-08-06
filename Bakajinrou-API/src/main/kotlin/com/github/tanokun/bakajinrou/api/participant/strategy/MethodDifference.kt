package com.github.tanokun.bakajinrou.api.participant.strategy

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant

interface MethodDifference {
    val participant: Participant

    data class MethodAdded(override val participant: Participant, val addedMethod: GrantedMethod): MethodDifference
    data class MethodRemoved(override val participant: Participant, val removedMethod: GrantedMethod): MethodDifference
}