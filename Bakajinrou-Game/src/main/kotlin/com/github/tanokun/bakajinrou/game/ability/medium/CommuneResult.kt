package com.github.tanokun.bakajinrou.game.ability.medium

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.ParticipantId

sealed interface CommuneResult {
    val mediumId: ParticipantId
    val targetId: ParticipantId

    sealed interface Success: CommuneResult

    data class FoundResult(val source: ResultSource, override val mediumId: ParticipantId, override val targetId: ParticipantId): Success
    data class IsNotDead(override val mediumId: ParticipantId, override val targetId: ParticipantId): Success
    data class NotFoundError(override val mediumId: ParticipantId, override val targetId: ParticipantId): CommuneResult
}