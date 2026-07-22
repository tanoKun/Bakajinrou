package com.github.tanokun.bakajinrou.api.advantage.using

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import kotlin.random.Random

/**
 * 位置交換の対象となる参加者を、候補者リストから選定します。
 */
class ExchangeSelector(private val random: Random) {

    /**
     * 指定された候補者リストから、交換の対象となる参加者を1人選定します。
     *
     * 自身と同じ、死んでいる参加者は対象外です。
     *
     * @param sideId 能力を使用する参加者のId
     * @param candidates 交換相手の候補となる参加者Id
     *
     * @return 選定された参加者のId
     */
    fun select(
        sideId: ParticipantId,
        candidates: Set<ParticipantId>,
    ): ParticipantId =
        (candidates - sideId).random(random)
}
