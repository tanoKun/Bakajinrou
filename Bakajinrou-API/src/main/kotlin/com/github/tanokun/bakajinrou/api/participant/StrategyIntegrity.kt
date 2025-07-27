package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.method.GrantedMethod

class StrategyIntegrity {
    private val activeMethods = arrayListOf<GrantedMethod>()

    fun exist(method: GrantedMethod): Boolean = activeMethods.any { it.uniqueId == method.uniqueId }

    fun enableMethod(method: GrantedMethod) {
        if (exist(method)) throw IllegalArgumentException("重複した手段です。")

        activeMethods.add(method)
    }

    fun disableMethod(method: GrantedMethod) {
        if (!exist(method)) throw IllegalArgumentException("登録されていない手段です。")

        activeMethods.removeIf { it.uniqueId == method.uniqueId }
    }
}