package com.github.tanokun.bakajinrou.api.ability

import com.github.tanokun.bakajinrou.api.translate.TranslationKey
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

enum class AbilityResultSource(val resultKey: TranslationKey) {
    WOLF(TranslationKeys.Ability.Result.WOLF),
    FOX(TranslationKeys.Ability.Result.FOX),
    CITIZENS(TranslationKeys.Ability.Result.CITIZENS),
    ;
}