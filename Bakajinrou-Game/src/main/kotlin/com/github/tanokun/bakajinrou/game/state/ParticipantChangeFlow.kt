package com.github.tanokun.bakajinrou.game.state

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantDifference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot

fun <T> Flow<ParticipantDifference>.distinctUntilChangedByParticipantOf(
    keySelector: (Participant) -> T,
): Flow<ParticipantDifference> = filterNot { difference ->
    val before = difference.before?.let(keySelector)
    val after = keySelector(difference.after)
    before == after
}
