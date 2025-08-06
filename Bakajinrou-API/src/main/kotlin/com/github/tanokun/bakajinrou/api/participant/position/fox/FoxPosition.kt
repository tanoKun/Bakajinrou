package com.github.tanokun.bakajinrou.api.participant.position.fox

import com.github.tanokun.bakajinrou.api.ability.AbilityResultSource
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

object FoxPosition: Position {
    override val prefixSource: PrefixSource = DefaultPrefix(TranslationKeys.Prefix.FOX)

    override val abilityResult: AbilityResultSource = AbilityResultSource.FOX
}