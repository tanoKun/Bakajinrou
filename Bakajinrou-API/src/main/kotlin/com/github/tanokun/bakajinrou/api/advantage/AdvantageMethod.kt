package com.github.tanokun.bakajinrou.api.advantage

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

abstract class AdvantageMethod: GrantedMethod {
    abstract override val assetKey: MethodAssetKeys.Advantage

    override val transportable: Boolean = true
}