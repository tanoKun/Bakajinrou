package com.github.tanokun.bakajinrou.api.ability

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.protect.ProtectVerificator
import com.github.tanokun.bakajinrou.api.protect.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

/**
 * 能力(Ability)の一種であり、「騎士」のように他者を守る能力を表す抽象クラス
 *
 * この能力は、それ自体が直接的な防御を行うのではなく、実行時に防御手段を生成します。
 */
abstract class ProtectAbility: Ability {
    /**
     * この能力の表示用アセットキー(固定で騎士を指す)
     */
    override val assetKey = MethodAssetKeys.Ability.PROTECT

    override val transportable: Boolean = false

    /**
     * この能力を行使した際に、加護として使用される 防御手段 を生成して取得します。
     *
     * @param verificator 防御手段が有効かどうかを検証するクラス
     * @return 実際に防御を行うための[ProtectiveMethod]のインスタンス
     */
    abstract fun protect(verificator: ProtectVerificator): ProtectiveMethod

    /**
     * この手段は譲渡不可能なため、このメソッドを呼び出すことはできません。
     * 誤って呼び出された場合は常に[UnsupportedOperationException]をスローします。
     *
     * @throws UnsupportedOperationException 常にスローされます。
     */
    @Deprecated(
        message = "ProtectAbility is not transferable.",
        level = DeprecationLevel.ERROR
    )
    override fun asTransferred(): GrantedMethod = throw UnsupportedOperationException()

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