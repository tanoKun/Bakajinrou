package com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.prefix.IdiotPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.TranslationKey

abstract class IdiotPosition(
    realKey: TranslationKey,
    idiotKey: TranslationKey,
): CitizensPosition() {
    override val prefixSource: PrefixSource = IdiotPrefix(realKey, idiotKey)
}