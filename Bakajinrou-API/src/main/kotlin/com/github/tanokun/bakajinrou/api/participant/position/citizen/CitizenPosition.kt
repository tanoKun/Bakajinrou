package com.github.tanokun.bakajinrou.api.participant.position.citizen

import com.github.tanokun.bakajinrou.api.ability.AbilityResultSource
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

object CitizenPosition: CitizensPosition() {
    override val prefixSource: PrefixSource = DefaultPrefix(TranslationKeys.Prefix.Citizens.CITIZEN)

    override val abilityResult: AbilityResultSource = AbilityResultSource.CITIZENS
}