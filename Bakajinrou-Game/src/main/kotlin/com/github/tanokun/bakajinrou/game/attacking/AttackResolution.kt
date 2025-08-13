package com.github.tanokun.bakajinrou.game.attacking

import com.github.tanokun.bakajinrou.api.attacking.AttackByMethodResult
import com.github.tanokun.bakajinrou.api.participant.ParticipantId

sealed interface AttackResolution {
    val attackerId: ParticipantId
    val victimId: ParticipantId
    val result: AttackByMethodResult

    data class Killed(
        override val attackerId: ParticipantId,
        override val victimId: ParticipantId,
        override val result: AttackByMethodResult.SucceedAttack
    ) : AttackResolution

    data class Alive(
        override val attackerId: ParticipantId,
        override val victimId: ParticipantId,
        override val result: AttackByMethodResult.Protected
    ) : AttackResolution
}