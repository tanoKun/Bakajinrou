package com.github.tanokun.bakajinrou.api.participant.position.fox

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Side
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

data object FoxPosition: Position {
    override val prefixSource: PrefixSource = DefaultPrefix(PrefixKeys.FOX)

    override val side: Side? = Side.FOX

    override val divinedAs: ResultSource = ResultSource.FOX
}