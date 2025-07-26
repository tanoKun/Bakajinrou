package com.github.tanokun.bakajinrou.game.scheduler

import com.github.tanokun.bakajinrou.game.scheduler.schedule.OnCancellationByOvertimeSchedule
import com.github.tanokun.bakajinrou.game.scheduler.schedule.OnlyOnceSchedule
import com.github.tanokun.bakajinrou.game.scheduler.schedule.TimeSchedule
import kotlin.reflect.KClass

abstract class GameScheduler(
    private val startTime: Long,
    schedules: List<TimeSchedule>,
): Runnable {
    private var leftTime: Long = startTime

    private val schedules = schedules.toMutableList()

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

    /**
     * スケジュールの追加を行います
     *
     * @throws IllegalStateException 起動後にスケジュールを入れてしまった場合
     */
    fun addSchedule(schedule: TimeSchedule) {
        if (isActive()) throw IllegalStateException("起動後にスケジュールは追加できません")

        schedules.add(schedule)
    }

    protected fun <E: TimeSchedule> tryCall(e: KClass<E>) {
        schedules
            .filterIsInstance(e.java)
            .forEach { it.tryCall(startSeconds = startTime, leftSeconds = leftTime) }
    }

    override fun run() {
        if (!isActive()) throw IllegalStateException("このスケジューラーはアクティブではありません。")

        schedules
            .filterNot { it is OnlyOnceSchedule }
            .forEach { it.tryCall(startSeconds = startTime, leftSeconds = leftTime) }

        leftTime--

        if (leftTime <= 0) {
            tryCall(OnCancellationByOvertimeSchedule::class)
            cancel()
        }
    }
}