package com.github.tanokun.bakajinrou.bukkit.scheduler.schedule

import kotlin.time.Duration

/**
 * 時間毎に、コールバック関数を呼び出します。
 *
 * @throws IllegalArgumentException 呼び出し間隔が一秒以下の時
 */
data class EveryTimeSchedule(
        val everyTime: Duration,
        override val callback: (Long) -> Unit
): TimeSchedule {
    private val everyTimeSeconds = everyTime.inWholeSeconds

    init {
        if (everyTime.inWholeSeconds < 1) throw IllegalArgumentException("最低一秒毎である必要があります。")
    }

    override fun tryCall(startSeconds: Long, leftSeconds: Long) {
        val passedTime = startSeconds - leftSeconds

        if (passedTime % everyTimeSeconds == 0L) callback(leftSeconds)
    }
}

infix fun Duration.every(callback: (Long) -> Unit): TimeSchedule = EveryTimeSchedule(this, callback)
