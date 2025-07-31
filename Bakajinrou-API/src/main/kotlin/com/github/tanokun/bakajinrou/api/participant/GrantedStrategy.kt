package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import java.util.*

interface GrantedStrategy {
    /**
     * 手段を参加者に付与します。
     * 全ストラテージーを通して、[GrantedMethod.uniqueId]が重複した方法は追加できません。
     *
     * @param method 追加したい手段
     *
     * @throws IllegalArgumentException 重複した手段を追加する場合
     */
    fun grant(method: GrantedMethod)

    /**
     * 手段を参加者から剥奪します。
     * 全ストラテージーを通して、[GrantedMethod.uniqueId]が存在しない手段は剥奪出来ません。
     *
     * @param method 削除したいアイテム
     *
     * @throws IllegalArgumentException 存在しない手段を剥奪する場合
     */
    fun remove(method: GrantedMethod)

    /**
     * @param uniqueId 取得したい手段のUUID
     *
     * @return 対応する手段
     */
    fun getMethod(uniqueId: UUID): GrantedMethod?

    /**
     * 呼び出し時点で、防御可能な手段を取得します。
     * Listのインデックスが若い順に使用優先度が高いです。
     *
     * @param holder 所有者
     *
     * @return 防御可能な手段
     */
    fun getActiveProtectiveMethods(holder: Participant): List<ProtectiveMethod>
}