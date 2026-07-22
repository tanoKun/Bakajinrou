package com.github.tanokun.bakajinrou.game.method.advantage.using

import com.github.tanokun.bakajinrou.api.participant.ParticipantId

sealed interface ExchangeInfo {
    val userId: ParticipantId

    data class Succeeded(
        override val userId: ParticipantId,
        val targetId: ParticipantId,
    ): ExchangeInfo

    data class NoTarget(
        override val userId: ParticipantId,
    ): ExchangeInfo
}
