package com.github.tanokun.bakajinrou.game.session

import com.github.tanokun.bakajinrou.api.WonInfo

sealed interface GameLifecycle {
    data object Preparing : GameLifecycle
    data object Running : GameLifecycle
    data class Finished(val result: WonInfo) : GameLifecycle
}
