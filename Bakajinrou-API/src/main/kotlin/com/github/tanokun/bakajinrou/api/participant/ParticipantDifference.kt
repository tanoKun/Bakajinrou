package com.github.tanokun.bakajinrou.api.participant

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot

data class ParticipantDifference(val before: Participant?, val after: Participant)

fun <T> Flow<ParticipantDifference>.distinctUntilChangedByParticipantOf(keySelector: (Participant) -> T) = this.filterNot {
    val before = it.before?.let { beforeParticipant -> keySelector(beforeParticipant) }
    val after = keySelector(it.after)

    before == after
}