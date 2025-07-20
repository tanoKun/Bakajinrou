package com.github.tanokun.bakajinrou.api.finishing

import com.github.tanokun.bakajinrou.api.participant.Participant

interface GameFinishDecider {
    /**
     * @param participants 全参加者
     *
     * [participants] の役職から、勝利条件を満たす参加者が存在する場合、
     * その陣営のフィニッシャーを返します。
     *
     * @return 勝利条件を満たしている陣営のフィニッシャー
     */
    fun decide(participants: List<Participant>): GameFinisher?
}