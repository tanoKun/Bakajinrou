package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.prefix.ComingOut
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import java.util.*

data class Participant(
    val uniqueId: UUID,
    val position: Position,
    internal val strategy: GrantedStrategy,
    internal val state: ParticipantStates =
        if (position is SpectatorPosition) ParticipantStates.DEAD
        else ParticipantStates.ALIVE,
    val comingOut: ComingOut? = null
) {

    /**
     * 状態を死亡状態にします。以下の状態では変更できません。
     * - 現在が死亡状態
     * - 現在が中断状態
     *
     * @return 死亡状態の参加者
     *
     * @throws IllegalStateException 変更できなかった場合
     */
    fun dead(): Participant {
        if (state == ParticipantStates.DEAD) throw IllegalStateException("既に死亡状態です。")
        if (state == ParticipantStates.SUSPENDED) throw IllegalStateException("中断状態から、死亡状態にすることはできません。")

        return copy(state = ParticipantStates.DEAD)
    }

    /**
     * 状態を生存状態にします。以下の状態では変更できません。
     * - 現在が中断状態でない場合
     *
     * @return 生存状態の参加者
     *
     * @throws IllegalStateException 変更できなかった場合
     */
    fun alive(): Participant {
        if (state != ParticipantStates.SUSPENDED) throw IllegalStateException("中断状態ではありません。")

        return copy(state = ParticipantStates.ALIVE)
    }

    /**
     * 状態を中断状態にします。以下の状態では変更できません。
     * - 現在が生存状態ではない場合
     *
     * @return 生存状態の参加者
     *
     * @throws IllegalStateException 変更できなかった場合
     */
    fun suspended(): Participant {
        if (state != ParticipantStates.ALIVE) throw IllegalStateException("生存状態ではありません。")

        return copy(state = ParticipantStates.SUSPENDED)
    }

    /**
     * カミングアウトをします。
     *
     * @param comingOut 明かす役職
     */
    fun comingOut(comingOut: ComingOut?) = copy(comingOut = comingOut)

    /**
     * @return 死亡状態か
     */
    fun isDead(): Boolean = state == ParticipantStates.DEAD

    /**
     * @return 中断状態か
     */
    fun isSuspended(): Boolean = state == ParticipantStates.SUSPENDED

    /**
     * @return スペクテイターであることを検出できるかどうか
     */
    fun isVisibleSpectators(): Boolean = isPosition<SpectatorPosition>() || state == ParticipantStates.DEAD

    /**
     * 陣営を比較します。
     */
    inline fun <reified T: Position> isPosition() = position is T

    fun getPrefix(viewer: Participant) = position.prefixSource.getVisibleSource(viewer, this)

    fun grantMethod(method: GrantedMethod) = copy(strategy = strategy.grant(method))

    fun removeMethod(method: GrantedMethod) = copy(strategy = strategy.remove(method))

    fun getGrantedMethod(uniqueId: UUID): GrantedMethod? = strategy.getMethod(uniqueId)

    fun getActiveProtectiveMethods(): List<ProtectiveMethod> = strategy.getActiveProtectiveMethods()
}