package com.github.tanokun.bakajinrou.api.participant.strategy

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.protection.method.ProtectiveMethod

data class GrantedStrategy(
    internal val strategies: Map<MethodId, GrantedMethod>,
) {
    /**
     * 手段を参加者に付与します。既に所持する手段は持つ取得することができません。
     *
     * @param method 追加したい手段
     *
     * @throws IllegalArgumentException 重複した手段を追加する場合
     */
    fun grant(method: GrantedMethod): GrantedStrategy {
        if (strategies.contains(method.methodId)) throw IllegalArgumentException("すでに所持している手段です。(methodId: ${method.methodId})")

        return this.copy(strategies = strategies + (method.methodId to method))
    }

    /**
     * 手段を参加者から剥奪します。
     *
     * @param method 削除したい手段
     *
     * @throws IllegalArgumentException 存在しない手段を剥奪する場合
     */
    fun remove(method: GrantedMethod): GrantedStrategy {
        if (!strategies.contains(method.methodId)) throw IllegalArgumentException("所有していない手段です。(methodId: ${method.methodId})")

        return this.copy(strategies = strategies - method.methodId)
    }

    /**
     * 手段を参加者から剥奪します。
     *
     * @param filter 削除する手段のフィルター
     *
     * @throws IllegalArgumentException 存在しない手段を剥奪する場合
     */
    fun removeAll(filter: (GrantedMethod) -> Boolean): GrantedStrategy {
        val passed = strategies.values
            .filter(filter)
            .map { it.methodId }

        return this.copy(strategies = strategies - passed)
    }

    /**
     * 手段を参加者から剥奪します。
     *
     * @param methods 削除したい手段の一覧
     *
     * @throws IllegalArgumentException 存在しない手段を剥奪する場合
     */
    fun removeAll(methods: Collection<GrantedMethod>): GrantedStrategy =
        methods.fold(this) { acc, method ->
            this.remove(method)
        }

    /**
     * @param methodId 取得したい手段のId
     *
     * @return 対応する手段
     */
    fun getMethod(methodId: MethodId): GrantedMethod? = strategies[methodId]

    /**
     * 呼び出し時点で、防御可能な手段を取得します。
     * Listのインデックスが若い順に使用優先度が高いです。
     *
     * @return 防御可能な手段
     */
    fun getValidProtectiveMethods(): List<ProtectiveMethod> =
        strategies.values
            .filterIsInstance<ProtectiveMethod>()
            .filter { it.isValid }
            .sortedBy { it.priority }
}