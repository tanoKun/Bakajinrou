package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

data class DefaultPrefix(private val prefixKey: PrefixKeys): PrefixSource {

    override fun getVisibleSource(viewer: Participant, target: Participant): PrefixKeys? {
        if (viewer.isDead()) return prefixKey
        if (viewer == target) return prefixKey

        return null
    }
}