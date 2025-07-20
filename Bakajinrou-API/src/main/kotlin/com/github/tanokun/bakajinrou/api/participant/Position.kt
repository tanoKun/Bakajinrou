package com.github.tanokun.bakajinrou.api.participant

interface Position {
    /**
     * ゲームが始まった瞬間の職業別の初期化処理を行います。
     */
    fun doAtStarting()
}