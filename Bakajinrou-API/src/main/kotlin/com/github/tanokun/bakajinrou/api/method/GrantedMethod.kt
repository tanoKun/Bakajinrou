package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

/**
 * 参加者が行動を行うために所有する「手段」を表すインターフェース
 *
 * 「手段」は、攻撃、防御、能力といった、ゲーム内でのあらゆるアクションの源泉となります。
 * このインターフェースを実装する各クラスが、具体的な振る舞いとルールを定義します。
 */
interface GrantedMethod {
    /**
     * この手段をユニークに識別するためのId。
     */
    val methodId: MethodId

    /**
     * この手段固有の識別Key
     */
    val assetKey: MethodAssetKeys

    /**
     * この手段が付与された理由(初期配布、譲渡など)を示します。
     */
    val reason: GrantedReason

    /**
     * この手段が他の参加者に譲渡可能かどうかを示します。
     */
    val transportable: Boolean

    /**
     * この手段が譲渡された際の新しいインスタンスを生成します。
     * 通常は、付与理由[reason] を [GrantedReason.TRANSFERRED] に変更したコピーを返します。
     *
     * @return 譲渡済みとしてマークされた、この手段の新しいインスタンス
     */
    fun asTransferred(): GrantedMethod
}