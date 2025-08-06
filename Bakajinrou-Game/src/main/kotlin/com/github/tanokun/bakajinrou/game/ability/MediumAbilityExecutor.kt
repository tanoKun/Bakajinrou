package com.github.tanokun.bakajinrou.game.ability

import com.github.tanokun.bakajinrou.api.ability.medium.CommuneResult
import com.github.tanokun.bakajinrou.api.ability.medium.MediumAbility
import java.util.*

class MediumAbilityExecutor(private val mediumAbility: MediumAbility) {
    fun commune(target: UUID): CommuneResult {
        return mediumAbility.commune(target)
    }
}