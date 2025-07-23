package com.github.tanokun.bakajinrou.bukkit.scheduler

import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.OnCancellationTimeSchedule
import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.TimeSchedule
import kotlin.reflect.KClass

abstract class GameScheduler(
    private val startTime: Long,
    private val timeSchedules: List<TimeSchedule>,
): Runnable {
    private var leftTime: Long = startTime

    init {
        if (startTime < 0) throw IllegalArgumentException("スケジューラーは1秒以上動かせる必要があります。")
    }

    /**
     * スケジューラーを起動します。2重起動、再起動は出来ません。
     *
     * @throws IllegalStateException 2重起動時
     * @throws IllegalStateException 既に停止されている場合
     */
    abstract fun launch()

    /**
     * スケジューラーを停止します。非稼働時に停止はできません。
     *
     * @throws IllegalStateException 非稼働時
     * @throws IllegalStateException 既に停止されている場合
     */
    abstract fun cancel()

    /**
     * スケジュールが実行中か判定します。
     *
     * @return スケジュールの状態
     */
    abstract fun isActive(): Boolean

    protected fun <E: TimeSchedule> tryCall(e: KClass<E>) {
        timeSchedules
            .filterIsInstance(e.java)
            .forEach { it.tryCall(startSeconds = startTime, leftSeconds = leftTime) }
    }

    override fun run() {
        if (!isActive()) throw IllegalStateException("このスケジューラーはアクティブではありません。")

        timeSchedules
            .filterNot { it is OnCancellationTimeSchedule }
            .forEach { it.tryCall(startSeconds = startTime, leftSeconds = leftTime) }

        leftTime--

        if (leftTime <= 0) cancel()
    }
}