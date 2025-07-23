package com.github.tanokun.bakajinrou.bukkit.scheduler.schedule

import com.github.tanokun.bakajinrou.bukkit.scheduler.CallbackOnSchedule

/**
 * タイムスケジュールに従って、コールバック関数を実行します。
 */
sealed interface TimeSchedule {
    val callback: CallbackOnSchedule

    /**
     * スケジュールの条件に当てはまっているとき、コールバック関数を呼び出します。
     */
    fun tryCall(startSeconds: Long, leftSeconds: Long)
}