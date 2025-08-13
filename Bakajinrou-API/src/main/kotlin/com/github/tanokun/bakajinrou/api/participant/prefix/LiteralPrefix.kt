package com.github.tanokun.bakajinrou.api.participant.prefix

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

class LiteralPrefix(private val prefixKey: PrefixKeys): PrefixSource {

    override fun getVisibleSource(viewer: Participant, target: Participant): PrefixKeys = prefixKey
}