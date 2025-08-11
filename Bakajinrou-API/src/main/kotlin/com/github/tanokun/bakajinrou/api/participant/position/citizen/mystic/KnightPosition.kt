package com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic

import com.github.tanokun.bakajinrou.api.ability.knight.RealProtectAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys

object KnightPosition: MysticPosition() {
    override val prefixSource: PrefixSource = DefaultPrefix( PrefixKeys.Mystic.KNIGHT)

    override fun inherentMethods(): List<GrantedMethod> {
        return listOf(RealProtectAbility(reason = GrantedReason.INITIALIZE))
    }
}