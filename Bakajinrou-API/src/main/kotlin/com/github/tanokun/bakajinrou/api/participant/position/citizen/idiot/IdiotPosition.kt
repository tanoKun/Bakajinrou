package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.prefix.IdiotPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys

abstract class IdiotPosition(
    realKey:  PrefixKeys.Idiot,
    fakeKey:  PrefixKeys.Mystic
): CitizensPosition() {
    override val prefixSource: PrefixSource = IdiotPrefix(realKey, fakeKey)
}