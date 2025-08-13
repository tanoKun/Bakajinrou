package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason

/**
 * ゲーム開始時、初期状態で所持している手段を表します。
 */
interface InitialMethod: GrantedMethod {

    /**
     * この手段が譲渡された際の新しいインスタンスを生成します。
     * 通常は、付与理由[reason] を [GrantedReason.CRAFTED] に変更したコピーを返します。
     *
     * @return クラフトとしてマークされた、この手段の新しいインスタンス
     */
    fun asCrafted(): GrantedMethod
}