package com.github.tanokun.bakajinrou.api.participant.position.wolf

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys

object MadmanPosition: Position {
    override val prefixSource: PrefixSource = DefaultPrefix(PrefixKeys.MADMAN)

    override val abilityResult: ResultSource = ResultSource.CITIZENS
}