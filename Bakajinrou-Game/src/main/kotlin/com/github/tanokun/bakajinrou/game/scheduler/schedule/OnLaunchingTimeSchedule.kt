package com.github.tanokun.bakajinrou.game.scheduler.schedule

import com.github.tanokun.bakajinrou.game.scheduler.CallbackOnSchedule

/**
 * スケジューラー開始されたときに呼び出されます。
 */
class OnLaunchingTimeSchedule(
    override val callback: CallbackOnSchedule
) : OnlyOnceSchedule {

    override fun tryCall(startSeconds: Long, leftSeconds: Long) = callback(leftSeconds)
}

fun onLaunching(callback: CallbackOnSchedule): TimeSchedule = OnLaunchingTimeSchedule(callback)