package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

sealed interface PrefixSource {

    fun getVisibleSource(viewer: Participant, target: Participant): PrefixKeys?
}