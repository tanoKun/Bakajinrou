package com.github.tanokun.bakajinrou.bukkit.scheduler

import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.TimeSchedule
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class JinrouGameScheduler(
    private val startTime: Long,
    private val timeSchedules: List<TimeSchedule>,
    private val plugin: Plugin
): BukkitRunnable() {
    private var start: Int = 0

    private var leftTime: Long = startTime

    init {
        if (startTime < 0) throw IllegalArgumentException("スケジューラーは1秒以上動かせる必要があります。")
    }

    fun start() {
        if (start == 1) throw IllegalStateException("二重起動はできません。")
        if (start > 1) throw IllegalStateException("二重起動はできません。")

        start == 1

        this.runTaskTimer(plugin, 20, 1)
    }

    override fun cancel() {
        if (start == 2) throw IllegalStateException("既に停止されています。")

        start == 2

        this.cancel()
    }

    override fun run() {
        timeSchedules.forEach { it.tryCall(startSeconds = startTime, leftSeconds = leftTime) }

        leftTime--
    }
}