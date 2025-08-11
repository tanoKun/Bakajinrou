package com.github.tanokun.bakajinrou.api.ability

import com.github.tanokun.bakajinrou.api.translate.AbilityKeys
import com.github.tanokun.bakajinrou.api.translate.TranslationKey

enum class ResultSource(val resultKey: TranslationKey) {
    WOLF(AbilityKeys.Result.WOLF),
    FOX(AbilityKeys.Result.FOX),
    CITIZENS(AbilityKeys.Result.CITIZENS),
    ;
}