package com.github.tanokun.bakajinrou.plugin.setting.factory

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.GrowingNotifier
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.HiddenPositionAnnouncer
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.QuartzDistribute
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.TimeAnnouncer
import com.github.tanokun.bakajinrou.plugin.setting.map.GameMap
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Location
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class GameMapTest {
    @Test
    @DisplayName("ゲームマップが作成したスケジュールのテスト")
    fun doScheduleCreatedByMapTest() {
        val startTime = 400L

        val map = GameMap(
            spawnLocation = mockk<Location>(),
            lobbyLocation = mockk<Location>(),
            startTime = startTime,
            delayToGiveQuartz = 90.seconds,
        )

        val timeAnnouncer: TimeAnnouncer = mockk(relaxed = true)
        val quartzDistributeMock: QuartzDistribute = mockk(relaxed = true)
        val growingNotifierMock: GrowingNotifier = mockk(relaxed = true)
        val hiddenPositionAnnouncerMock: HiddenPositionAnnouncer = mockk(relaxed = true)

        val jinrouGame: JinrouGame = mockk()

        val schedules = map.createSchedules(
            timeAnnouncer, quartzDistributeMock, growingNotifierMock, hiddenPositionAnnouncerMock, jinrouGame
        )

        for (time in 0..startTime) {
            schedules.forEach { it.tryCall(startSeconds = startTime, leftSeconds = startTime - time) }
        }

        verify(exactly = 401) { timeAnnouncer.showRemainingTimeActionBar(jinrouGame, any()) }
        verify(exactly = 6) { quartzDistributeMock.distributeQuartzToSurvivors(jinrouGame) }
        verify(exactly = 5) { growingNotifierMock.growCitizens(jinrouGame) }
        verify(exactly = 1) { growingNotifierMock.announceGlowingStart(jinrouGame, 3) }
        verify(exactly = 1) { hiddenPositionAnnouncerMock.notifyWolfsAndFox(jinrouGame) }
    }
}