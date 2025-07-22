package com.github.tanokun.bakajinrou.bukkit.scheduler

import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.OnCancellationTimeSchedule
import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.TimeSchedule

abstract class GameScheduler(
    private val startTime: Long,
    private val timeSchedules: List<TimeSchedule>,
): Runnable {
    private var leftTime: Long = startTime

    init {
        if (startTime < 0) throw IllegalArgumentException("スケジューラーは1秒以上動かせる必要があります。")
    }

    /**
     * スケジュールを起動します。2重起動、再起動は出来ません。
     *
     * @throws IllegalStateException 2重起動、再起動時
     */
    abstract fun start()

    /**
     * スケジュールを停止します。非稼働時に停止はできません。
     *
     * @throws IllegalStateException 非稼働時に停止
     */
    abstract fun cancel()

    override fun run() {
        timeSchedules.forEach { it.tryCall(startSeconds = startTime, leftSeconds = leftTime) }

        leftTime--

        if (leftTime <= 0) {
            timeSchedules
                .filterIsInstance<OnCancellationTimeSchedule>()
                .forEach { it.tryCall(startSeconds = startTime, leftSeconds = leftTime) }

        }
    }
}