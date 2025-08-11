package com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic

import com.github.tanokun.bakajinrou.api.ability.medium.CorrectCommuneAbility
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys

object MediumPosition: MysticPosition() {
    override val prefixSource: PrefixSource = DefaultPrefix( PrefixKeys.Mystic.MEDIUM)

    override fun inherentMethods(): List<GrantedMethod> {
        return listOf(CorrectCommuneAbility(reason = GrantedReason.INITIALIZE))
    }
}