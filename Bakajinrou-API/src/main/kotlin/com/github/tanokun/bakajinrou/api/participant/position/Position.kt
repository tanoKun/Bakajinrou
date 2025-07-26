package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import java.util.*

interface Position {
    val prefix: Prefix

    /**
     * ゲームが始まった瞬間の職業別の初期化処理を行います。
     */
    fun doAtStarting(uniqueId: UUID)

    fun isVisibleBy(viewer: Participant): Boolean = viewer.isPosition<SpectatorPosition>()
}