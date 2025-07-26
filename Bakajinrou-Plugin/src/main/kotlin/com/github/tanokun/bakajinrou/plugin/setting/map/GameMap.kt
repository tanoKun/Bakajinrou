package com.github.tanokun.bakajinrou.plugin.setting.map

import com.github.tanokun.bakajinrou.game.scheduler.schedule.TimeSchedule
import com.github.tanokun.bakajinrou.game.scheduler.schedule.arranged
import com.github.tanokun.bakajinrou.game.scheduler.schedule.every
import com.github.tanokun.bakajinrou.plugin.cache.BukkitPlayerNameCache
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
    /**
     *
     * @param planner Bukkitへの通知の委託先
     * @param bukkitPlayerNameCache プレイヤー名のキャッシュ
     *
     * @return ゲームのスケジュール
     *
     * @see GameSchedules
     * @see com.github.tanokun.bakajinrou.plugin.cache.BukkitPlayerNameCache
     */
    fun createSchedules(
        planner: GameSchedules, bukkitPlayerNameCache: BukkitPlayerNameCache
    ): List<TimeSchedule> = listOf(
        1.seconds every { leftSeconds ->
            planner.showLeftTime(leftSeconds)
        },

        delayToGiveQuartz every { _ ->
            planner.giveQuartzToSurvivedParticipants()
        },

        1.minutes every schedule@ { leftSeconds ->
            if (leftSeconds.seconds > 5.minutes) return@schedule

            planner.growCitizens()
        },

        6.minutes arranged {
            planner.notifyParticipantsOfGrowing()
        },

        3.minutes arranged {
            planner.notifyWolfsAndFox(bukkitPlayerNameCache)
        }
    )
}