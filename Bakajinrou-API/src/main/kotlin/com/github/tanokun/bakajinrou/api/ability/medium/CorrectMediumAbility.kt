package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.JinrouGame
import java.util.*

class CorrectMediumAbility(private val jinrouGame: JinrouGame, uniqueId: UUID = UUID.randomUUID()): MediumAbility(uniqueId) {
    override fun commune(target: UUID): CommuneResult {
        val participant = jinrouGame.getParticipant(target) ?: return CommuneResult.NotFoundError

        if (participant.isDead()) return CommuneResult.NotDeadError

        return CommuneResult.FoundResult(participant.position.abilityResult.resultKey)
    }
}