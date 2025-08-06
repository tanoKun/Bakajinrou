package com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic

import com.github.tanokun.bakajinrou.api.participant.prefix.DefaultPrefix
import com.github.tanokun.bakajinrou.api.participant.prefix.PrefixSource
import com.github.tanokun.bakajinrou.api.translate.TranslationKeys

object KnightPosition: MysticPosition() {
    override val prefixSource: PrefixSource = DefaultPrefix(TranslationKeys.Prefix.Citizens.Mystic.KNIGHT)
}