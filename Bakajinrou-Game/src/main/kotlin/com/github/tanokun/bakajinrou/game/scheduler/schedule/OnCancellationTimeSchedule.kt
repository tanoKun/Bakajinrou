package com.github.tanokun.bakajinrou.game.scheduler.schedule

import com.github.tanokun.bakajinrou.game.scheduler.CallbackOnSchedule

/**
 * スケジューラーが時間切れ、強制キャンセルされた場合に呼び出されます。
 */
class OnCancellationTimeSchedule(
    override val callback: CallbackOnSchedule
) : TimeSchedule {

    override fun tryCall(startSeconds: Long, leftSeconds: Long) = callback(leftSeconds)
}

fun onCancellation(callback: CallbackOnSchedule): TimeSchedule = OnCancellationTimeSchedule(callback)