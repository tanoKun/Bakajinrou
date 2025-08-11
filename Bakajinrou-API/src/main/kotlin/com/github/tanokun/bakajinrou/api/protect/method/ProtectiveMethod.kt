package com.github.tanokun.bakajinrou.api.protect.method

import com.github.tanokun.bakajinrou.api.attack.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protect.ActivationPriority
import com.github.tanokun.bakajinrou.api.protect.ProtectResult
import com.github.tanokun.bakajinrou.api.protect.ProtectVerificator
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

/**
 * 攻撃を防ぐことに特化した「防御手段」を表す抽象クラス
 *
 * 全ての防御手段は、このクラスを継承し、具体的な防御ロジックを実装します。
 */
abstract class ProtectiveMethod: GrantedMethod {

    /**
     * この防御手段の翻訳キー。常に[MethodAssetKeys.Protective]のサブタイプとなります。
     */
    abstract override val assetKey: MethodAssetKeys.Protective

    /**
     * 複数の防御手段が同時に有効な場合に、どちらが先に消費されるかを決定する優先度。
     * 通常、値が高いほど優先的に消費されます。
     */
    abstract val priority: ActivationPriority

    /**
     * この防御手段が現在有効であるか、を示します。
     *
     * このプロパティの具体的な真偽値は、注入される [ProtectVerificator] によって決定されるべきです。
     */
    abstract val isValid: Boolean

    /**
     * 防御手段は、原則として譲渡可能です。
     */
    override val transportable: Boolean = true

    /**
     * 指定された攻撃を基に、この防御手段が有効かを判定します。
     * 有効はどうかは関係なく、成功か失敗で判断されます。
     *
     * @param method 攻撃手段
     *
     * @return 防御結果
     */
    abstract fun verifyProtect(method: AttackMethod): ProtectResult

    /**
     * この防御手段が譲渡された際の新しいインスタンスを生成します。
     *
     * @param participantId 譲渡先の参加者Id
     *
     * @return 譲渡済みとしてマークされる。この防御手段の新しいインスタンス
     */
    abstract fun asTransferred(participantId: ParticipantId): ProtectiveMethod

    /**
     * このメソッドは [ProtectiveMethod] では使用すべきではありません。
     * 代わりに [asTransferred] を使用してください。
     *
     * @throws UnsupportedOperationException 常にスローされます。
     */
    @Deprecated(
        message = "Use asTransferred(verificator: ProtectVerificator)",
        level = DeprecationLevel.ERROR
    )
    override fun asTransferred(): GrantedMethod = throw UnsupportedOperationException()
}