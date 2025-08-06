package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translate.TranslationKey
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

enum class ComingOut(private val translationKey: TranslationKey): PrefixSource {
    LAST_WOLF(TranslationKeys.Prefix.ComingOut.LAST_WOLF),
    FORTUNE(TranslationKeys.Prefix.ComingOut.FORTUNE),
    MEDIUM(TranslationKeys.Prefix.ComingOut.MEDIUM),
    KNIGHT(TranslationKeys.Prefix.ComingOut.KNIGHT),
    ;

    override fun getVisibleSource(viewer: Participant, target: Participant): TranslationKey = translationKey
}