package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.ability.fortune.FakeDivineAbility
import com.github.tanokun.bakajinrou.api.method.InitialMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

object IdiotAsFortunePosition: IdiotPosition(
    realKey =  PrefixKeys.Idiot.FORTUNE,
    fakeKey =  PrefixKeys.Mystic.FORTUNE
) {

    override fun inherentMethods(): List<InitialMethod> {
        return listOf(FakeDivineAbility(reason = GrantedReason.INITIALIZED))
    }
}