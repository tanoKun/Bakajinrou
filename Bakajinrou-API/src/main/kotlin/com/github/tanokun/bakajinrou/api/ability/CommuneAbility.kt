package com.github.tanokun.bakajinrou.api.ability

import com.github.tanokun.bakajinrou.api.ability.medium.CommuneResultSource
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

/**
 * 能力 の一種である **霊媒** を表す抽象クラス
 *
 * - 死亡している参加者を霊媒し、「人狼」「妖狐」「市民」を判定します。
 * - 実装クラスで [commune] を具体化し、判定ロジックを提供します。
 *
 * @property methodId Id
 */
abstract class CommuneAbility: Ability {
    abstract override val methodId: MethodId

    override val assetKey = MethodAssetKeys.Ability.COMMUNE

    override val transportable: Boolean = false

    /**
     * 対象の参加者を霊媒し、その役職を判定します。
     *
     * @param target 霊媒対象の参加者
     *
     * @return 霊媒結果
     */
    abstract fun commune(target: Participant): CommuneResultSource

    override fun asTransferred(): GrantedMethod = throw IllegalStateException("この手段は譲渡できません。")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DivineAbility

        return methodId == other.methodId
    }

    override fun hashCode(): Int {
        return methodId.hashCode()
    }
}