package com.github.tanokun.bakajinrou.bukkit.scheduler

import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.arranged
import com.github.tanokun.bakajinrou.bukkit.scheduler.schedule.every
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class TimeScheduleTest {
    @Test
    @DisplayName("120秒間、N秒毎で呼び出されるコールバック関数")
    fun everyTimeScheduleShouldCallCallbackEveryNSeconds() {
        var calledCount = 0
        val startTime = 120L
        val schedule = 4.seconds every { calledCount++ }

        for (i in startTime downTo 0L) {
            schedule.tryCall(startSeconds = startTime, leftSeconds = i)
        }

        assertEquals(31, calledCount)
    }

    @Test
    @DisplayName("N秒毎で呼び出されるコールバック関数 (端数あり)")
    fun everyTimeScheduleShouldNotCallback() {
        var called = false
        val schedule = 4.seconds every { called = true }

        schedule.tryCall(startSeconds = 61L, leftSeconds = 60L)

        assertFalse(called)
    }

    @Test
    @DisplayName("1秒毎以上が必要")
    fun everyTimeScheduleShouldThrowIfLessThan1Second() {
        val exception = assertFailsWith<IllegalArgumentException> {
            0.5.seconds every {}
        }

        assertEquals("最低一秒毎である必要があります。", exception.message)
    }

    @Test
    @DisplayName("特定の残り時間で呼び出されるコールバック関数")
    fun arrangedTimeScheduleShouldCallCallbackAtSpecificTime() {
        var called = false
        val schedule = 10.seconds arranged { called = true }

        schedule.tryCall(startSeconds = 60L, leftSeconds = 10L)

        assertTrue(called)
    }

    @Test
    @DisplayName("特定の残り時間以外では呼び出されないコールバック関数")
    fun arrangedTimeScheduleShouldNotCallCallbackAtOtherTimes() {
        var called = false
        val schedule = 10.seconds arranged { called = true }

        schedule.tryCall(startSeconds = 60L, leftSeconds = 11L)
        schedule.tryCall(startSeconds = 60L, leftSeconds = 9L)
        assertFalse(called)
    }
}