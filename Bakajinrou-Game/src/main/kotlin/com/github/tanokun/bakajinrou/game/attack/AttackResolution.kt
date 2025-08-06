package com.github.tanokun.bakajinrou.game.attack

import com.github.tanokun.bakajinrou.api.attack.AttackByMethodResult
import com.github.tanokun.bakajinrou.api.participant.Participant

sealed interface AttackResolution {

    data class Killed(
        val attacker: Participant,
        val result: AttackByMethodResult.SucceedAttack
    ) : AttackResolution

    data class Alive(
        val attacker: Participant,
        val result: AttackByMethodResult.Protected
    ) : AttackResolution
}