package com.github.tanokun.bakajinrou.game.position

import com.github.tanokun.bakajinrou.game.PlayerStates
import java.util.UUID

class JinrouPlayer(
    val uniqueId: UUID,
    val position: Position
) {
    private var state: PlayerStates = PlayerStates.SURVIVED

    /**
     * 状態を死亡状態にします。
     * また現在の状態がゲーム中断の場合、状態は変更されません。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun dead(): Boolean {
        if (state == PlayerStates.SUSPENDED) return false
        if (state == PlayerStates.DEAD) return false

        state = PlayerStates.DEAD

        return true
    }

    /**
     * 現在の状態がゲーム中断の時のみ、状態を生存状態にします。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun survived(): Boolean {
        if (state == PlayerStates.SURVIVED) return false
        if (state == PlayerStates.DEAD) return false

        state = PlayerStates.SURVIVED

        return true
    }

    /**
     * 現在の状態が生存状態の時のみ、状態をゲーム中断にします。
     *
     * @return 変更可能 true, 不可能 false
     */
    fun suspended(): Boolean {
        if (state == PlayerStates.SURVIVED) return false
        if (state == PlayerStates.DEAD) return false

        state = PlayerStates.SUSPENDED

        return true
    }
}