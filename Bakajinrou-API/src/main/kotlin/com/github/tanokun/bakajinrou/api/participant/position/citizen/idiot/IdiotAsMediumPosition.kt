package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.ability.medium.FakeCommuneAbility
import com.github.tanokun.bakajinrou.api.method.InitialMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

data object IdiotAsMediumPosition: IdiotPosition(
    realKey =  PrefixKeys.Idiot.MEDIUM,
    fakeKey =  PrefixKeys.Mystic.MEDIUM
) {

    override fun inherentMethods(): List<InitialMethod> {
        return listOf(FakeCommuneAbility(reason = GrantedReason.INITIALIZED))
    }
}