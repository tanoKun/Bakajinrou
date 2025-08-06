package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translate.TranslationKey

sealed interface PrefixSource {

    fun getVisibleSource(viewer: Participant, target: Participant): TranslationKey?
}