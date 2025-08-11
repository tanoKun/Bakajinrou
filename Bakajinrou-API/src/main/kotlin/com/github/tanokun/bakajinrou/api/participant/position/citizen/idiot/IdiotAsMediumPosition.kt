package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.ability.medium.FakeCommuneAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys

object IdiotAsMediumPosition: IdiotPosition(
    realKey =  PrefixKeys.Idiot.MEDIUM,
    fakeKey =  PrefixKeys.Mystic.MEDIUM
) {

    override fun inherentMethods(): List<GrantedMethod> {
        return listOf(FakeCommuneAbility(reason = GrantedReason.INITIALIZE))
    }
}