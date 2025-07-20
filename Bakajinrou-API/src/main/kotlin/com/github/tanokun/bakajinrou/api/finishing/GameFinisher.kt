package com.github.tanokun.bakajinrou.api.finishing

interface GameFinisher {

    /**
     * ゲーム終了を、各陣営に適した形で知らせます。
     */
    fun notifyFinish()
}