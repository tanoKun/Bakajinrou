package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant

/**
 * viewerを基準にしてプレフィックスを決定します。順に優先度が高い条件です。
 * - 観察者が観戦または死亡 -> 完全なプレフィックス
 * - 観察者がViewerの条件を満たしている -> 生存時のプレフィックス
 * - 観察者が被観察者と一緒 -> 生存時のプレフィックス
 *
 * Viewerの条件は[Position.isVisibleBy]を参照
 *
 * @see Position.isVisibleBy
 **/
class Prefix(
    val revealedPrefix: String,
    val defaultPrefix: String
) {

    /**
     * [viewer]と[target]の状況から、適するプレフィックスを選びます。
     * また、当てはまる条件が存在しない場合、nullを返します。
     *
     * @param viewer 観察者
     * @param target 非観察者
     *
     * @return 適するプレフィックス
     */
    fun resolvePrefix(viewer: Participant, target: Participant): String? {
        if (viewer.isPosition<SpectatorPosition>() || viewer.state == ParticipantStates.DEAD) return revealedPrefix
        if (target.position.isVisibleBy(viewer) || target == viewer) return defaultPrefix

        return null
    }
}