package com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic

import com.github.tanokun.bakajinrou.api.ability.fortune.CorrectDivineAbility
import com.github.tanokun.bakajinrou.api.method.InitialMethod
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

object FortunePosition: MysticPosition() {
    override val prefixSource: PrefixSource = DefaultPrefix( PrefixKeys.Mystic.FORTUNE)

    override fun inherentMethods(): List<InitialMethod> {
        return listOf(CorrectDivineAbility(reason = GrantedReason.INITIALIZED))
    }
}