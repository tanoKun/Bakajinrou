package com.github.tanokun.bakajinrou.game.viewer

import com.github.tanokun.bakajinrou.api.player.PlayerId

data class GameViewerChange(
    val playerId: PlayerId,
    val before: GameViewer?,
    val after: GameViewer?,
)
