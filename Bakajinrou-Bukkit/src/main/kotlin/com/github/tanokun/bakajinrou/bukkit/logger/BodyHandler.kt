package com.github.tanokun.bakajinrou.bukkit.logger

import com.github.tanokun.bakajinrou.api.participant.Participant
import java.util.*

interface BodyHandler {

    /**
     * [of]に適するプレイヤーの死体を作成します。
     */
    fun createBody(of: Participant)

    /**
     * 全ての死体を削除します。
     */
    fun deleteBodies()

    /**
     * 登録されている死体を、[to]プレイヤーに表示します。
     */
    fun showBodies(to: UUID)
}