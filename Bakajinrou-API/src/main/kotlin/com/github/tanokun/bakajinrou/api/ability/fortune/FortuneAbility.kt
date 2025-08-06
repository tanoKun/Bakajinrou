package com.github.tanokun.bakajinrou.api.ability.fortune

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys
import java.util.*

abstract class FortuneAbility(override val uniqueId: UUID): GrantedMethod {
    override val assetKey = TranslationKeys.Method.Ability.FORTUNE

    abstract fun divine(target: UUID): DivineResult

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FortuneAbility

        return uniqueId == other.uniqueId
    }

    override fun hashCode(): Int {
        return uniqueId.hashCode()
    }
}