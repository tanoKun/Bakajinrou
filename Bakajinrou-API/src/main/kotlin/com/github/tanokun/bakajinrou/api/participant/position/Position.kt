package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope

interface Position {
    /**
     * ゲームが始まった瞬間の職業別の初期化処理を行います。
     */
    fun doAtStarting(self: Participant, participants: ParticipantScope.All)
}