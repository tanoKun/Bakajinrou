package com.github.tanokun.bakajinrou.bukkit.scheduler.schedule

import kotlin.time.Duration

/**
 * 特定の残り時間の時、コールバック関数を呼び出します。
 */
data class ArrangedTimeSchedule(
        val arrangedTime: Duration,
        override val callback: (Long) -> Unit
): TimeSchedule {
    private val arrangedTimeSeconds = arrangedTime.inWholeSeconds

    override fun tryCall(startSeconds: Long, leftSeconds: Long) {
        if (leftSeconds == arrangedTimeSeconds) callback(leftSeconds)
    }
}

infix fun Duration.arranged(callback: (Long) -> Unit): TimeSchedule = ArrangedTimeSchedule(this, callback)