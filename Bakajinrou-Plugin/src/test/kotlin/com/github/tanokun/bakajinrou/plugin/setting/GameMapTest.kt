package com.github.tanokun.bakajinrou.plugin.setting

import com.github.tanokun.bakajinrou.plugin.cache.BukkitPlayerNameCache
import com.github.tanokun.bakajinrou.plugin.schedule.GameSchedules
import io.mockk.*
import org.bukkit.Location
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class GameMapTest {
    @Test
    @DisplayName("ゲームマップが作成したスケジュールのテスト")
    fun doScheduleCreatedByMapTest() {
        val startTime = 400L

        val map = GameMap(spawnLocation = mockk<Location>(),
            lobbyLocation = mockk<Location>(),
            startTime = startTime,
            delayToGiveQuartz = 90.seconds,
        )

        val gameSchedules = mockk<GameSchedules>()
        val schedules = map.createSchedules(gameSchedules, BukkitPlayerNameCache())

        every { gameSchedules.showLeftTime(any()) } just Runs
        every { gameSchedules.giveQuartzToSurvivedParticipants() } just Runs
        every { gameSchedules.growCitizens() } just Runs
        every { gameSchedules.notifyParticipantsOfGrowing() } just Runs
        every { gameSchedules.notifyWolfsAndFox(any()) } just Runs

        for (time in 0..startTime) {
            schedules.forEach { it.tryCall(startSeconds = startTime, leftSeconds = startTime - time) }
        }

        verify(exactly = 401) { gameSchedules.showLeftTime(any()) }
        verify(exactly = 5) { gameSchedules.giveQuartzToSurvivedParticipants() }
        verify(exactly = 5) { gameSchedules.growCitizens() }
        verify(exactly = 1) { gameSchedules.notifyParticipantsOfGrowing() }
        verify(exactly = 1) { gameSchedules.notifyWolfsAndFox(any()) }
        confirmVerified(gameSchedules)
    }
}