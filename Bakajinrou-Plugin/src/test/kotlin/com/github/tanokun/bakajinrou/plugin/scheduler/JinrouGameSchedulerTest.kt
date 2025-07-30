package com.github.tanokun.bakajinrou.plugin.scheduler

import com.github.tanokun.bakajinrou.game.scheduler.CallbackOnSchedule
import com.github.tanokun.bakajinrou.game.scheduler.schedule.every
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onCancellation
import com.github.tanokun.bakajinrou.game.scheduler.schedule.onLaunching
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.*
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import kotlin.time.Duration.Companion.seconds

class JinrouGameSchedulerTest {
    private lateinit var server: ServerMock

    private lateinit var plugin: Plugin

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.createMockPlugin()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    @DisplayName("二重起動は出来ない")
    fun cannotLaunchWhenRunning() {
        val scheduler = JinrouGameScheduler(
            startTime = 10,
            timeSchedules = listOf(),
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )

        scheduler.launch()
        assertThrows<IllegalStateException>("二重起動はできません。") {
            scheduler.launch()
        }
    }

    @Test
    @DisplayName("既に停止されたスケジューラーは起動できない")
    fun cannotLaunchAlreadyCancel() {
        val scheduler = JinrouGameScheduler(
            startTime = 10,
            timeSchedules = listOf(),
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )

        scheduler.launch()
        scheduler.cancel()
        assertThrows<IllegalStateException>("既に停止されたスケジュールです。") {
            scheduler.launch()
        }
    }

    @Test
    @DisplayName("まだ起動されていないスケジューラーは停止できない")
    fun cannotCancelNotYetLaunch() {
        val scheduler = JinrouGameScheduler(
            startTime = 10,
            timeSchedules = listOf(),
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )
        
        assertThrows<IllegalStateException>("まだ起動されていないスケジュールです。") {
            scheduler.cancel()
        }
    }

    @Test
    @DisplayName("既に停止されているスケジューラーは停止できない")
    fun cannotCancelAlreadyCancel() {
        val scheduler = JinrouGameScheduler(
            startTime = 10,
            timeSchedules = listOf(),
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )

        scheduler.launch()
        scheduler.cancel()
        assertThrows<IllegalStateException>("まだ起動されていないスケジュールです。") {
            scheduler.cancel()
        }
    }

    @Test
    @DisplayName("強制停止での、スケジュール呼び出し")
    fun onForcedCancellationTest() {
        val callbackMock = mockk<CallbackOnSchedule>(relaxed = true)

        val schedule = listOf(
            onCancellation(callbackMock)
        )

        val scheduler = JinrouGameScheduler(
            startTime = 10,
            timeSchedules = schedule,
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )

        scheduler.launch()
        scheduler.cancel()

        verify(exactly = 1) { callbackMock(any()) }
    }

    @Test
    @DisplayName("時間切れ停止での、スケジュール呼び出し")
    fun onOvertimeCancellationTest() {
        val callbackMock = mockk<CallbackOnSchedule>(relaxed = true)

        val schedule = listOf(
            onCancellation(callbackMock)
        )

        val scheduler = JinrouGameScheduler(
            startTime = 4,
            timeSchedules = schedule,
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )

        scheduler.launch()
        server.scheduler.performTicks(100)

        verify(exactly = 1) { callbackMock(any()) }
    }

    @Test
    @DisplayName("1秒単位でのスケジューリング")
    fun onCalledEverySeconds() {
        val callbackMock = mockk<CallbackOnSchedule>(relaxed = true)

        val schedule = listOf(
            1.seconds every callbackMock
        )

        val scheduler = JinrouGameScheduler(
            startTime = 10,
            timeSchedules = schedule,
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )

        scheduler.launch()
        server.scheduler.performTicks(40)

        verify(exactly = 2) { callbackMock(any()) }
    }

    @Test
    @DisplayName("開始時のスケジュール")
    fun onCalledLaunchingSchedule() {
        val callbackMock = mockk<CallbackOnSchedule>(relaxed = true)

        val schedule = listOf(
            onLaunching(callbackMock)
        )

        val scheduler = JinrouGameScheduler(
            startTime = 10,
            timeSchedules = schedule,
            bukkitScheduler = server.scheduler,
            plugin = plugin
        )

        scheduler.launch()
        server.scheduler.performTicks(40)

        verify(exactly = 1) { callbackMock(any()) }
    }
}
