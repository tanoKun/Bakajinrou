package com.github.tanokun.bakajinrou.plugin.participant.prefix

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import net.kyori.adventure.text.Component

/**
 * viewerを基準にしてプレフィックスを決定します。
 **/
interface Prefix {
    /**
     * [viewer]と[target]の状況から、適するプレフィックスを選びます。
     * また、当てはまる条件が存在しない場合、nullを返します。
     *
     * @param viewer 観察者
     * @param target 非観察者
     *
     * @return 適するプレフィックス
     */
    fun resolvePrefix(viewer: Participant, target: Participant): Component?

    /**
     * @return 観察者が、スペクテイターもしくは死亡状態である場合は、true
     */
    fun isRevealed(viewer: Participant): Boolean = viewer.isPosition<SpectatorPosition>() || viewer.state == ParticipantStates.DEAD
}