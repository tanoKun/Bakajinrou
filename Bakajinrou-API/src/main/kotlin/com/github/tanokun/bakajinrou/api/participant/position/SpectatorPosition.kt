package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.ability.AbilityResultSource
import com.github.tanokun.bakajinrou.api.participant.prefix.LiteralPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

class SpectatorPosition: Position {
    override val prefixSource: PrefixSource = LiteralPrefix(TranslationKeys.Prefix.SPECTATOR)

    override val abilityResult: AbilityResultSource = AbilityResultSource.CITIZENS

}