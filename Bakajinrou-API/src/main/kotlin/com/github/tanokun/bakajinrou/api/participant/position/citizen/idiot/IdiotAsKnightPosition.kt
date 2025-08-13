package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.ability.knight.FakeProtectAbility
import com.github.tanokun.bakajinrou.api.method.InitialMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

object IdiotAsKnightPosition: IdiotPosition(
    realKey =  PrefixKeys.Idiot.KNIGHT,
    fakeKey =  PrefixKeys.Mystic.KNIGHT
) {

    override fun inherentMethods(): List<InitialMethod> {
        return listOf(FakeProtectAbility(reason = GrantedReason.INITIALIZED))
    }
}