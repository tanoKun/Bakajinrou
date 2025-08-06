package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.JinrouGame
import java.util.*

class CorrectFortuneAbility(private val jinrouGame: JinrouGame, uniqueId: UUID = UUID.randomUUID()): FortuneAbility(uniqueId) {
    override fun divine(target: UUID): DivineResult {
        val participant = jinrouGame.getParticipant(target) ?: return DivineResult.NotFoundError

        return DivineResult.FoundResult(participant.position.abilityResult.resultKey, participant)
    }
}