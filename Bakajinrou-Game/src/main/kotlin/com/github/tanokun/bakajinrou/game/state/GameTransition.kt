package com.github.tanokun.bakajinrou.game.state

import com.github.tanokun.bakajinrou.api.JinrouGame

data class GameTransition<out T>(
    val game: JinrouGame,
    val result: T,
)
