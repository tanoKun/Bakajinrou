package com.github.tanokun.bakajinrou.plugin.setting.map

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.scheduler.schedule.TimeSchedule
import com.github.tanokun.bakajinrou.game.scheduler.schedule.arranged
import com.github.tanokun.bakajinrou.game.scheduler.schedule.every
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.GrowingNotifier
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.HiddenPositionAnnouncer
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.QuartzDistribute
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.TimeAnnouncer
import org.bukkit.Location
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class GameMap(
    val spawnLocation: Location,
    val lobbyLocation: Location,
    val startTime: Long,
    val delayToGiveQuartz: Duration
) {

    fun createSchedules(
        timeAnnouncer: TimeAnnouncer,
        quartzDistribute: QuartzDistribute,
        growingNotifier: GrowingNotifier,
        hiddenPositionAnnouncer: HiddenPositionAnnouncer,
        jinrouGame: JinrouGame,
    ): List<TimeSchedule> = listOf(
        1.seconds every { leftSeconds ->
            timeAnnouncer.showRemainingTimeActionBar(jinrouGame, leftSeconds)
        },

        delayToGiveQuartz every { leftSeconds ->
            quartzDistribute.distributeQuartzToSurvivors(jinrouGame)

            if (startTime == leftSeconds) quartzDistribute.distributeQuartzToSurvivors(jinrouGame)
        },

        6.minutes arranged {
            growingNotifier.announceGlowingStart(jinrouGame, 3)
        },

        1.minutes every schedule@ { leftSeconds ->
            if (leftSeconds.seconds > 5.minutes) return@schedule

            growingNotifier.growCitizens(jinrouGame)
        },

        3.minutes arranged {
            hiddenPositionAnnouncer.notifyWolfsAndFox(jinrouGame)
        }
    )
}