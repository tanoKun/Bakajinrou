package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope

interface Position {
    val prefix: Prefix

    val publicPosition: Position

    /**
     * ゲームが始まった瞬間の職業別の初期化処理を行います。
     */
    fun doAtStarting(self: Participant, participants: ParticipantScope.All)

    fun isVisibleBy(viewer: Participant): Boolean = viewer.isPosition<SpectatorPosition>()

    fun publicPositionName(): String = publicPosition.prefix.revealedPrefix
}