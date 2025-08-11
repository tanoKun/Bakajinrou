package com.github.tanokun.bakajinrou.api.participant.position.citizen

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys

object CitizenPosition: CitizensPosition() {
    override val prefixSource: PrefixSource = DefaultPrefix( PrefixKeys.CITIZEN)

    override val abilityResult: ResultSource = ResultSource.CITIZENS
}