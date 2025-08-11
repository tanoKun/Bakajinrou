package com.github.tanokun.bakajinrou.game.ability.fortune

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.ParticipantId

sealed interface DivineResult {
    val fortuneId: ParticipantId
    val targetId: ParticipantId

    data class FoundResult(val source: ResultSource, override val fortuneId: ParticipantId, override val targetId: ParticipantId): DivineResult
    data class NotFoundError(override val fortuneId: ParticipantId, override val targetId: ParticipantId): DivineResult
}