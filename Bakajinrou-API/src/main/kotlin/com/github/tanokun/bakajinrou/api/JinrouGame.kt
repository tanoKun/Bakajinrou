package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import java.util.*

class JinrouGame(
    val participants: List<Participant>,
) {

    /**
     * @see Participant.dead
     */
    fun changeToDead(uuid: UUID): Boolean = getParticipant(uuid).dead()

    /**
     * @see Participant.survived
     */
    fun changeToSurvived(uuid: UUID): Boolean = getParticipant(uuid).survived()

    /**
     * @see Participant.suspended
     */
    fun changeToSuspended(uuid: UUID): Boolean = getParticipant(uuid).suspended()

    private fun getParticipant(uuid: UUID): Participant =
        participants.firstOrNull { it.uniqueId == uuid } ?: throw IllegalArgumentException("存在しない参加者UUID: $uuid")
}