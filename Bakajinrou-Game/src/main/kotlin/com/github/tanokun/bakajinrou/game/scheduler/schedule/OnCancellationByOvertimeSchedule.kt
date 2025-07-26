package com.github.tanokun.bakajinrou.game.scheduler.schedule

import com.github.tanokun.bakajinrou.game.scheduler.CallbackOnSchedule

/**
 * スケジューラーが時間切れでキャンセルされた時に呼び出されます。
 */
class OnCancellationByOvertimeSchedule(
    override val callback: CallbackOnSchedule
) : OnlyOnceSchedule {

    override fun tryCall(startSeconds: Long, leftSeconds: Long) = callback(leftSeconds)
}

fun onCancellationByOvertime(callback: CallbackOnSchedule): TimeSchedule = OnCancellationByOvertimeSchedule(callback)