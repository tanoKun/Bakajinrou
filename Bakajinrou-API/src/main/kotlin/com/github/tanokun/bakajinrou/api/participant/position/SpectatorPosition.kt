package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.prefix.LiteralPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

data object SpectatorPosition: Position {
    override val prefixSource: PrefixSource = LiteralPrefix(PrefixKeys.SPECTATOR)

    override val abilityResult: ResultSource = ResultSource.CITIZENS
}