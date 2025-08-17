package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.prefix.ComingOut
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.protection.method.ProtectiveMethod

/**
 * ゲームへの参加者を表す不変なデータクラスです。
 * `participantId` によって一意に識別されます。
 *
 * @param participantId 参加者を一意に識別するId
 * @param position 参加者の役職
 * @param strategy 参加者に付与された手段
 * @param state 参加者の現在の状態
 * @param comingOut 参加者がカミングアウト役職。なければnull
 */
data class Participant(
    val participantId: ParticipantId,
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
     * 参加者が死亡状態であるかを確認します。
     *
     * @return 死亡状態であれば `true`
     */
    fun isDead(): Boolean = state == ParticipantStates.DEAD

    /**
     * 参加者が中断状態であるかを確認します。
     *
     * @return 中断状態であれば `true`
     */
    fun isSuspended(): Boolean = state == ParticipantStates.SUSPENDED

    /**
     * 参加者が生存状態であるかを確認します。
     *
     * @return 生存状態であれば `true`
     */
    fun isAlive(): Boolean = state == ParticipantStates.ALIVE

    /**
     * この参加者が観戦者リストを閲覧できるかどうかを判定します。
     * 死亡しているか、元々が [SpectatorPosition] の役職である場合に閲覧可能です。
     *
     * @return 観戦者リストを閲覧できる場合は `true`
     */
    fun isVisibleSpectators(): Boolean = isPosition<SpectatorPosition>() || state == ParticipantStates.DEAD

    /**
     * 参加者が指定された [Position] であるかを確認します。
     *
     * @return 指定された [Position] と一致する場合は `true`
     */
    inline fun <reified T: Position> isPosition() = position is T

    /**
     * 指定された観察者から見た、この参加者のプレフィックスを取得します。
     *
     * @param viewer 観察者となる [Participant]
     *
     * @return 表示されるべきプレフィックス情報
     */
    fun getPrefix(viewer: Participant) = position.prefixSource.getVisibleSource(viewer, this)

    /**
     * 参加者に新しい手段を付与します。
     *
     * @param method 付与する [GrantedMethod]
     *
     * @return 手段が付与された新しい [Participant] インスタンス
     */
    fun grantMethod(method: GrantedMethod) = copy(strategy = strategy.grant(method))

    /**
     * 参加者から指定された手段を削除します。
     *
     * @param method 削除する [GrantedMethod]
     *
     * @return 手段が削除された新しい [Participant] インスタンス
     */
    fun removeMethod(method: GrantedMethod) = copy(strategy = strategy.remove(method))

    /**
     * 指定された条件に一致するすべての手段を参加者から削除します。
     *
     * @param filter 削除する [GrantedMethod] を判定するラムダ式
     *
     * @return 条件に合う手段が削除された新しい [Participant] インスタンス
     */
    fun removeAll(filter: (GrantedMethod) -> Boolean) = copy(strategy = strategy.removeAll(filter))

    /**
     * 指定されたコレクションに含まれるすべての手段を参加者から削除します。
     *
     * @param methods 削除対象の [GrantedMethod] のコレクション
     *
     * @return 手段が削除された新しい [Participant] インスタンス
     */
    fun removeAll(methods: Collection<GrantedMethod>) = copy(strategy = strategy.removeAll(methods))

    /**
     * 一意なIdを使用して、参加者に付与された手段を検索します。
     *
     * @param uniqueId 検索する手段の [MethodId]
     *
     * @return 見つかった場合は [GrantedMethod]、見つからなければ `null`
     */
    fun getGrantedMethod(uniqueId: MethodId): GrantedMethod? = strategy.getMethod(uniqueId)

    /**
     * 参加者が指定されたIdの手段を保持しているか確認します。
     *
     * @param uniqueId 確認する手段の [MethodId]
     *
     * @return 手段を保持していれば `true`
     */
    fun hasGrantedMethod(uniqueId: MethodId): Boolean = strategy.getMethod(uniqueId) != null

    /**
     * 参加者が現在行使可能な、すべての有効な保護手段を取得します。
     *
     * @return 有効な [ProtectiveMethod] のリスト
     */
    fun getValidProtectiveMethods(): List<ProtectiveMethod> = strategy.getValidProtectiveMethods()

    /**
     * ２つの [Participant] がIdだけでなく、すべてのプロパティにおいて完全に一致するかを比較します。
     * 主にテストやデバッグでの使用を想定しています。
     *
     * @param other 比較対象のオブジェクト
     *
     * @return すべてのプロパティが一致する場合は `true`
     */
    fun completelyEquals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Participant) return false

        if (participantId != other.participantId) return false
        if (position != other.position) return false
        if (strategy != other.strategy) return false
        if (state != other.state) return false
        if (comingOut != other.comingOut) return false

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Participant) return false

        if (participantId != other.participantId) return false
        return true
    }

    override fun hashCode(): Int = participantId.hashCode()
}