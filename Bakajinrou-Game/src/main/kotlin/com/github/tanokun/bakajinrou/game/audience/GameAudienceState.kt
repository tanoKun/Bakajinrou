package com.github.tanokun.bakajinrou.game.audience

import com.github.tanokun.bakajinrou.api.player.PlayerId

data class GameAudienceState(val spectators: Set<PlayerId>)
