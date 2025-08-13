package com.github.tanokun.bakajinrou.api.ability

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys

/**
 * 能力 の一種である **占い** を表す抽象クラス
 *
 * 参加者を占い、「人狼」「妖狐」「市民」のいずれかを判定します。
 * 他の能力や手段と異なり、途中で付与されることはなく、通常は役職に紐づいています。
 * 実装クラスで [divine] を具体的に実装し、判定ロジックを提供します。
 */
abstract class DivineAbility: Ability {
    /**
     * この能力の表示用アセットキー(固定で占いを指す)
     */
    override val assetKey = MethodAssetKeys.Ability.DIVINE

    override val transportable: Boolean = false

    /**
     * 対象の参加者を占い、その役割を判定します。
     *
     * @param target 占う対象の参加者 Id
     *
     * @return 占い結果
     */
    abstract fun divine(target: Participant): ResultSource

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

        other as DivineAbility

        return methodId == other.methodId
    }

    override fun hashCode(): Int {
        return methodId.hashCode()
    }
}