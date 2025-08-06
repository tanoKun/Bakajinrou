package com.github.tanokun.bakajinrou.api.participant.strategy

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import java.util.*

data class GrantedStrategy(
    internal val strategies: Map<UUID, GrantedMethod>,
) {
    /**
     * 手段を参加者に付与します。既に所持する手段は持つ取得することができません。
     *
     * @param method 追加したい手段
     *
     * @throws IllegalArgumentException 重複した手段を追加する場合
     */
    fun grant(method: GrantedMethod): GrantedStrategy {
        if (strategies.contains(method.uniqueId)) throw IllegalArgumentException("すでに所持している手段です。")

        return this.copy(strategies = strategies + (method.uniqueId to method))
    }

    /**
     * 手段を参加者から剥奪します。
     *
     * @param method 削除したいアイテム
     *
     * @throws IllegalArgumentException 存在しない手段を剥奪する場合
     */
    fun remove(method: GrantedMethod): GrantedStrategy {
        if (strategies.contains(method.uniqueId)) throw IllegalArgumentException("所有していない手段です。")

        return this.copy(strategies = strategies - method.uniqueId)
    }

    /**
     * @param uniqueId 取得したい手段のUUID
     *
     * @return 対応する手段
     */
    fun getMethod(uniqueId: UUID): GrantedMethod? = strategies[uniqueId]

    /**
     * 呼び出し時点で、防御可能な手段を取得します。
     * Listのインデックスが若い順に使用優先度が高いです。
     *
     * @param holder 所有者
     *
     * @return 防御可能な手段
     */
    fun getActiveProtectiveMethods(): List<ProtectiveMethod> =
        strategies.values
            .filterIsInstance<ProtectiveMethod>()
            .filter { it.isActive }
            .sortedBy { it.priority }
}