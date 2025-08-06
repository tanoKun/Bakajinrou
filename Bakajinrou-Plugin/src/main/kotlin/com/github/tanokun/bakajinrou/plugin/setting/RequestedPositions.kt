package com.github.tanokun.bakajinrou.plugin.setting

import com.github.tanokun.bakajinrou.api.translate.TranslationKey
import com.github.tanokun.bakajinrou.plugin.localization.UITranslationKeys

enum class RequestedPositions(val translationKey: TranslationKey) {
    WOLF(UITranslationKeys.Formatter.Participant.WOLF),
    MADMAN(UITranslationKeys.Formatter.Participant.MADMAN),
    IDIOT(UITranslationKeys.Formatter.Participant.IDIOT),
    FORTUNE(UITranslationKeys.Formatter.Participant.Mystic.FORTUNE),
    MEDIUM(UITranslationKeys.Formatter.Participant.Mystic.MEDIUM),
    KNIGHT(UITranslationKeys.Formatter.Participant.Mystic.KNIGHT),
    FOX(UITranslationKeys.Formatter.Participant.FOX),
}