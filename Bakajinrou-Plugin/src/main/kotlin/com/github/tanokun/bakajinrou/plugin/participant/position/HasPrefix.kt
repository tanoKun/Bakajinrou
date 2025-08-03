package com.github.tanokun.bakajinrou.plugin.participant.position

import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.prefix.Prefix

interface HasPrefix: Position {
    val prefix: Prefix

    val abilityResult: AbilityResult
}