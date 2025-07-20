package com.github.tanokun.bakajinrou.api.scheduler

interface GameScheduler {

    /**
     * スケジュールを開始します。
     *
     * @throws java.lang.IllegalStateException 二重で開始されたとき
     */
    fun start()

    /**
     * スケジュールを停止します。
     *
     * @throws java.lang.IllegalStateException 二重で停止されたとき
     */
    fun cancel()
}