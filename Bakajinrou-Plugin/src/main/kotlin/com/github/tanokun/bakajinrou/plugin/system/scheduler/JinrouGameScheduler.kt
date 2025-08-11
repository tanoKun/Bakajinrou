package com.github.tanokun.bakajinrou.plugin.system.scheduler

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class JinrouGameScheduler(
    startTime: Duration,
    private val bukkitScheduler: BukkitScheduler,
    private val plugin: Plugin
) : GameScheduler(startTime), Runnable {
    private var bukkitTask: BukkitTask? = null

    override fun launch() {
        super.launch()

        bukkitTask = bukkitScheduler.runTaskTimer(plugin, this, 20, 20)
    }

    override fun abort() {
        super.abort()

        bukkitTask?.cancel()
    }

    override fun overtime() { bukkitTask?.cancel() }

    override fun run() { advance(1.seconds) }
}