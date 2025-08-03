package com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.position.citizen.IdiotPosition
import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.position.HasPrefix
import com.github.tanokun.bakajinrou.plugin.participant.prefix.DefaultPrefix
import net.kyori.adventure.text.format.TextColor

abstract class IdiotAsJobPosition(fake: String, color: TextColor): IdiotPosition, HasPrefix {
    override val prefix: DefaultPrefix = DefaultPrefix(revealedPrefix = "$fake(バカ)", defaultPrefix = fake, color = color)

    override val abilityResult: AbilityResult = AbilityResult.Citizens
}