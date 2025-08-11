package com.github.tanokun.bakajinrou.api.ability

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.protect.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

/**
 * 能力 の一種である **騎士** を表す抽象クラス。
 *
 * 手段によって守ります。
 */
abstract class ProtectAbility: Ability {
    /**
     * この能力の表示用アセットキー(固定で騎士を指す)
     */
    override val assetKey = MethodAssetKeys.Ability.PROTECT

    override val transportable: Boolean = false

    /**
     * 守るための手段を取得します。
     *
     * @return 防御手段
     */
    abstract fun protect(): ProtectiveMethod

    override fun asTransferred(): GrantedMethod = throw IllegalStateException("この手段は譲渡できません。")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProtectAbility

        return methodId == other.methodId
    }

    override fun hashCode(): Int {
        return methodId.hashCode()
    }
}