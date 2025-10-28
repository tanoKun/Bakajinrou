package com.github.tanokun.bakajinrou.api.participant.position.wolf

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.ability.knight.FakeProtectAbility
import com.github.tanokun.bakajinrou.api.method.InitialMethod
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

data class MadmanPosition(private val hasFakeProtectAbility: Boolean): Position {
    override val prefixSource: PrefixSource = DefaultPrefix(PrefixKeys.MADMAN)

    override val abilityResult: ResultSource = ResultSource.CITIZENS

    override fun inherentMethods(): List<InitialMethod> {
        if (hasFakeProtectAbility) return listOf(FakeProtectAbility(reason = GrantedReason.INITIALIZED))

        return super.inherentMethods()
    }
}