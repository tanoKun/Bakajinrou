package com.github.tanokun.bakajinrou.game.audience

import com.github.tanokun.bakajinrou.api.player.PlayerId

/** ゲーム外から観戦しているプレイヤーのスナップショットです。 */
data class GameAudience(val spectators: Set<PlayerId>)
