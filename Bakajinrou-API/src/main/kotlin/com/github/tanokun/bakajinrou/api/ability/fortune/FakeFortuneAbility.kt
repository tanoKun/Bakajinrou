package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.AbilityResultSource
import java.util.*

class FakeFortuneAbility(private val jinrouGame: JinrouGame, uniqueId: UUID = UUID.randomUUID()): FortuneAbility(uniqueId) {
    override fun divine(target: UUID): DivineResult {
        val participant = jinrouGame.getParticipant(target) ?: return DivineResult.NotFoundError
        val randomResult = AbilityResultSource.entries.random()

        return DivineResult.FoundResult(randomResult.resultKey, participant)
    }
}