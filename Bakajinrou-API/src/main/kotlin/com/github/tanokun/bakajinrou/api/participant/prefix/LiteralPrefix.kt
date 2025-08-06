package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translate.TranslationKey

class LiteralPrefix(private val translationKey: TranslationKey): PrefixSource {

    override fun getVisibleSource(viewer: Participant, target: Participant): TranslationKey = translationKey
}