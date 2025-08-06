package com.github.tanokun.bakajinrou.api.ability.medium

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.AbilityResultSource
import java.util.*
import kotlin.random.Random

class FakeMediumAbility(private val jinrouGame: JinrouGame, uniqueId: UUID = UUID.randomUUID()): MediumAbility(uniqueId) {
    override fun commune(target: UUID): CommuneResult {
        jinrouGame.getParticipant(target) ?: return CommuneResult.NotFoundError
        val randomResult = AbilityResultSource.entries.random()

        if (Random.nextInt(4) == 0) return CommuneResult.NotFoundError

        return CommuneResult.FoundResult(randomResult.resultKey)
    }
}