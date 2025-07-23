package com.github.tanokun.bakajinrou.plugin.schedule

import com.github.tanokun.bakajinrou.bukkit.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.OnCancellationTimeSchedule
import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.TimeSchedule
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

class JinrouGameScheduler(
    startTime: Long,
    timeSchedules: List<TimeSchedule>,
    private val bukkitScheduler: BukkitScheduler,
    private val plugin: Plugin
) : GameScheduler(startTime, timeSchedules) {
    private var state: SchedulerState = SchedulerState.NOT_YET_LAUNCH

    private var bukkitTask: BukkitTask? = null

    override fun launch() {
        if (state == SchedulerState.ACTIVE) throw IllegalStateException("二重起動はできません。")
        if (state == SchedulerState.CANCELED) throw IllegalStateException("既に停止されたスケジューラーです。")

        state = SchedulerState.ACTIVE

        bukkitTask = bukkitScheduler.runTaskTimer(plugin, this, 20, 1)
    }

    override fun cancel() {
        if (state == SchedulerState.NOT_YET_LAUNCH) throw IllegalStateException("まだ起動されていないスケジューラーです。")
        if (state == SchedulerState.CANCELED) throw IllegalStateException("既に停止されています。")

        state = SchedulerState.CANCELED

        bukkitTask?.cancel()
        tryCall(OnCancellationTimeSchedule::class)
    }

    override fun isActive(): Boolean = state == SchedulerState.ACTIVE

    private enum class SchedulerState {
        NOT_YET_LAUNCH,
        ACTIVE,
        CANCELED;
    }
}