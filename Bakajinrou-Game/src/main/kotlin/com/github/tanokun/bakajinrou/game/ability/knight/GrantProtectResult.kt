package com.github.tanokun.bakajinrou.game.ability.knight

import com.github.tanokun.bakajinrou.api.participant.ParticipantId

sealed interface GrantProtectResult {
    val knightId: ParticipantId
    val targetId: ParticipantId

    data class Granted(override val knightId: ParticipantId, override val targetId: ParticipantId): GrantProtectResult
    data class NotFoundError(override val knightId: ParticipantId, override val targetId: ParticipantId): GrantProtectResult
}