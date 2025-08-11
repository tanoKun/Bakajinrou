package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.ability.knight.FakeProtectAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys

object IdiotAsKnightPosition: IdiotPosition(
    realKey =  PrefixKeys.Idiot.KNIGHT,
    fakeKey =  PrefixKeys.Mystic.KNIGHT
) {

    override fun inherentMethods(): List<GrantedMethod> {
        return listOf(FakeProtectAbility(reason = GrantedReason.INITIALIZE))
    }
}