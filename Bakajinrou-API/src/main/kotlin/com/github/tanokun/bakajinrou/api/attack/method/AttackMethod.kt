package com.github.tanokun.bakajinrou.api.attack.method

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys

abstract class AttackMethod: GrantedMethod {
    abstract override val assetKey: MethodAssetKeys.Attack

    override val transportable: Boolean = true
}