package com.github.tanokun.bakajinrou.game.audience

import com.github.tanokun.bakajinrou.api.player.PlayerId

sealed interface AudienceChange {
    val playerId: PlayerId

    data class Joined(override val playerId: PlayerId): AudienceChange
    data class Left(override val playerId: PlayerId): AudienceChange
}
