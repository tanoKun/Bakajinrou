package com.github.tanokun.bakajinrou.bukkit.scheduler.schedule

import com.github.tanokun.bakajinrou.bukkit.scheduler.CallbackOnSchedule

class OnCancellationTimeSchedule(
    override val callback: CallbackOnSchedule
) : TimeSchedule {

    override fun tryCall(startSeconds: Long, leftSeconds: Long) = callback(leftSeconds)
}

fun onCancellation(callback: CallbackOnSchedule): TimeSchedule = OnCancellationTimeSchedule(callback)