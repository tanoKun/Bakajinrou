package com.github.tanokun.bakajinrou.api.method

import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

/**
 * 行動を行うために必要な 手段 を表します。
 * 手段 には振る舞いが存在し、実際の処理を行います。
 */
interface GrantedMethod {
    val methodId: MethodId

    val assetKey: MethodAssetKeys

    val reason: GrantedReason

    val transportable: Boolean

    fun asTransferred(): GrantedMethod
}