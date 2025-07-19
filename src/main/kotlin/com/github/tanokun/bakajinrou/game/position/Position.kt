package com.github.tanokun.bakajinrou.game.position

import java.util.UUID

interface Participant {
    val uniqueId: UUID

    /**
     * ゲームが始まった瞬間の職業別の初期化処理
     */
    fun doAtStarting()

    /**
     * この陣営が勝った場合の、職業別の処理
     */
    fun doAtVictoryFinishing()

    /**
     * この陣営が負けた場合の、職業別の処理
     */
    fun doAtLoseFinishing()

    /**
     * 状態を死亡状態にします。
     * また現在の状態がゲーム中断の場合、状態は変更されません。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun dead(): Boolean

    /**
     * 現在の状態がゲーム中断の時のみ、状態を生存状態にします。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun survived(): Boolean

    /**
     * 現在の状態が生存状態の時のみ、状態をゲーム中断にします。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun suspended(): Boolean
}