package com.github.tanokun.bakajinrou.plugin.common.setting

import com.github.tanokun.bakajinrou.api.translation.PrefixKeys

enum class RequestedPositions(val formatKey: PrefixKeys) {
    WOLF(PrefixKeys.WOLF),
    MADMAN(PrefixKeys.MADMAN),
    IDIOT( PrefixKeys.IDIOT),
    FORTUNE(PrefixKeys.Mystic.FORTUNE),
    MEDIUM(PrefixKeys.Mystic.MEDIUM),
    KNIGHT(PrefixKeys.Mystic.KNIGHT),
    FOX(PrefixKeys.FOX),
}