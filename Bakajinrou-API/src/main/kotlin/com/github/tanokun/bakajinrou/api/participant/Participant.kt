package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import java.util.*

data class Participant(
    val uniqueId: UUID,
    val position: Position,
    private val strategy: GrantedStrategy
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

    fun grantMethod(method: GrantedMethod) = strategy.grant(method)

    fun removeMethod(method: GrantedMethod) = strategy.remove(method)

    fun getGrantedMethod(uniqueId: UUID): GrantedMethod? = strategy.getMethod(uniqueId)

    fun getActiveProtectiveMethods(): List<ProtectiveMethod> = strategy.getActiveProtectiveMethods(this)
}