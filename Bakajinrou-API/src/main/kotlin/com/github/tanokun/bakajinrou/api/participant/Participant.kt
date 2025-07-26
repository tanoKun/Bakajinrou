package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.protection.Protection
import java.util.*

data class Participant(
    val uniqueId: UUID,
    val position: Position,
    private val protection: Protection
) {
    var state: ParticipantStates = ParticipantStates.SURVIVED
        private set

    /**
     * 状態を死亡状態にします。
     * また現在の状態がゲーム中断の場合、状態は変更されません。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun dead(): Boolean {
        if (state == ParticipantStates.DEAD) return false
        if (state == ParticipantStates.SUSPENDED) return false

        state = ParticipantStates.DEAD

        return true
    }

    /**
     * 現在の状態がゲーム中断の時のみ、状態を生存状態にします。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun survived(): Boolean {
        if (state != ParticipantStates.SUSPENDED) return false

        state = ParticipantStates.SURVIVED

        return true
    }

    /**
     * 現在の状態が生存状態の時のみ、状態をゲーム中断にします。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun suspended(): Boolean {
        if (state == ParticipantStates.SUSPENDED) return false
        if (state == ParticipantStates.DEAD) return false

        state = ParticipantStates.SUSPENDED

        return true
    }

    /**
     * プレイヤーが何らかの理由で攻撃を無効かできるか
     *
     * @return 防御できる理由
     */
    fun hasProtection() = protection.hasProtection()

    /**
     * この参加者を非観察者として、プレフィックスを選びます。
     *
     * @param viewer 観察者
     */
    fun resolvePrefix(viewer: Participant) = position.prefix.resolvePrefix(viewer = viewer, target = this)

    /**
     * @return スペクテイターであることを検出できるかどうか
     */
    fun isVisibleSpectators(): Boolean = isPosition<SpectatorPosition>() || state == ParticipantStates.DEAD

    /**
     * 陣営を比較します。
     */
    inline fun <reified T: Position> isPosition() = position is T
}