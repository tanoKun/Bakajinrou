package com.github.tanokun.bakajinrou.game.logger

import java.util.*

interface BodyHandler {

    /**
     * [of]に適するプレイヤーの死体を作成します。
     */
    fun createBody(of: UUID)

    /**
     * 全ての死体を削除します。
     */
    fun deleteBodies()

    /**
     * 登録されている死体を、[to]プレイヤーに表示します。
     */
    fun showBodies(to: UUID)
}