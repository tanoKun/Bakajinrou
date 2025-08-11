package com.github.tanokun.bakajinrou.api.attack.method

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

/**
 * 他の参加者を攻撃するための「攻撃手段」を表す抽象クラス
 *
 * 全ての攻撃手段は、このクラスを継承します。
 * 実際の攻撃処理は、このクラスのインスタンスと関連するドメインサービス([com.github.tanokun.bakajinrou.api.attack.AttackVerificator])によって実行されます。
 */
abstract class AttackMethod: GrantedMethod {
    abstract override val assetKey: MethodAssetKeys.Attack

    override val transportable: Boolean = true
}