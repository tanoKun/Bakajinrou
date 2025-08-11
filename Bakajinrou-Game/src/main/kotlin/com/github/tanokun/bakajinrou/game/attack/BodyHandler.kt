package com.github.tanokun.bakajinrou.game.attack

import com.github.tanokun.bakajinrou.api.participant.ParticipantId

interface BodyHandler {

    /**
     * [of]に適する参加者の死体を作成します。
     */
    fun createBody(of: ParticipantId)

    /**
     * 全ての死体を削除します。
     */
    fun deleteBodies()

    /**
     * 登録されている死体を、[to]参加者に表示します。
     */
    fun showBodies(to: ParticipantId)
}