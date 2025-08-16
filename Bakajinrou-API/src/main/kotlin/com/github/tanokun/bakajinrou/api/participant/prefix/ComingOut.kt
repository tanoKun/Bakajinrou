package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

enum class ComingOut(val translationKey: PrefixKeys.ComingOut): PrefixSource {
    LAST_WOLF(PrefixKeys.ComingOut.LAST_WOLF),
    FORTUNE(PrefixKeys.ComingOut.FORTUNE),
    MEDIUM(PrefixKeys.ComingOut.MEDIUM),
    KNIGHT(PrefixKeys.ComingOut.KNIGHT),
    ;

    override fun getVisibleSource(viewer: Participant, target: Participant): PrefixKeys = translationKey
}