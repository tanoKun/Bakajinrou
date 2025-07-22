package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import java.util.*

class JinrouGame(
    val participants: List<Participant>,
) {

    /**
     * @see Participant.dead
     */
    fun changeToDead(uniqueId: UUID): Boolean = getParticipant(uniqueId).dead()

    /**
     * @see Participant.survived
     */
    fun changeToSurvived(uniqueId: UUID): Boolean = getParticipant(uniqueId).survived()

    /**
     * @see Participant.suspended
     */
    fun changeToSuspended(uniqueId: UUID): Boolean = getParticipant(uniqueId).suspended()

    private fun getParticipant(uniqueId: UUID): Participant =
        participants.firstOrNull { it.uniqueId == uniqueId } ?: throw IllegalArgumentException("存在しない参加者UUID: $uniqueId")
}