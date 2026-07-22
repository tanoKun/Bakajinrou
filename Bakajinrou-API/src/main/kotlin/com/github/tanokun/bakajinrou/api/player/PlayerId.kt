package com.github.tanokun.bakajinrou.api.player

import java.util.UUID

/** ゲームに関わるプレイヤーを、参加形態に依存せず識別するIDです。 */
data class PlayerId(val uniqueId: UUID)

fun UUID.asPlayerId() = PlayerId(this)
