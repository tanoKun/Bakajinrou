package com.github.tanokun.bakajinrou.api.attack.method

import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.api.translate.TranslationKey
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys
import java.util.*

data class ArrowMethod(override val uniqueId: UUID = UUID.randomUUID()): AttackMethod() {
    override val assetKey: TranslationKey = TranslationKeys.Method.Attack.ARROW
}