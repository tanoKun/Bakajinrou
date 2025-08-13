package com.github.tanokun.bakajinrou.api.protection

import com.github.tanokun.bakajinrou.api.participant.ParticipantId

/**
 * 防御手段が有効であるかを検証するためのインターフェースです。
 *
 * ドメイン層がUI層などの外部の状態に直接依存することなく、防御の状態を問い合わせるために使用されます。
 */
interface ProtectVerificator {

    /**
     * 防御手段が実際に有効であるかを判定します。
     *
     * ドメインだけでは表現できない、UI上の状態での判定が可能です。
     * 特定の防御効果を持つアイテムが装備されているかなどを判定するために実装されます。
     *
     * @return 有効 true, 無効 false
     */
    fun isValid(): Boolean

    /**
     * 対象を変更した検証インスタンスを作成します。
     *
     * @param participantId 対象の参加者Id
     *
     * @return 対象を変更した検証インスタンス
     */
    fun copy(participantId: ParticipantId): ProtectVerificator
}