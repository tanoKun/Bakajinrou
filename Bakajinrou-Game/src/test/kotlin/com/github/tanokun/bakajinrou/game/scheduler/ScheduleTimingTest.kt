package com.github.tanokun.bakajinrou.game.scheduler

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ScheduleTimingTest {

    private fun makeInProgressSequence(start: Duration, increments: List<Duration>): List<ScheduleState.Active.InProgress> {
        val list = mutableListOf<ScheduleState.Active.InProgress>()
        var passed = Duration.ZERO

        for (inc in increments) {
            passed += inc
            val inProgress = mockk<ScheduleState.Active.InProgress>()
            every { inProgress.passedTime } returns passed
            every { inProgress.remainingTime } returns (start - passed)
            list.add(inProgress)
        }

        return list
    }

    @Test
    @DisplayName("every は指定間隔ごとの最初の InProgress を流す")
    fun everyEmitsOnInterval() = runBlocking {
        val start = 10.seconds
        val increments = listOf(1.seconds, 1.seconds, 1.seconds, 1.seconds, 1.seconds)
        val seq = makeInProgressSequence(start, increments)

        val emitted = flowOf(*seq.toTypedArray()).every(2.seconds).toList()

        assertEquals(3, emitted.size)
        assertEquals(1.seconds, emitted[0].passedTime)
        assertEquals(3.seconds, emitted[1].passedTime)
        assertEquals(5.seconds, emitted[2].passedTime)
    }

    @Test
    @DisplayName("moment は正確に指定した passedTime の InProgress のみを流す")
    fun momentMatchesExactPassedTime() = runBlocking {
        val start = 10.seconds
        val increments = listOf(1.seconds, 2.seconds, 3.seconds) // 結果の passed:1,3,6
        val seq = makeInProgressSequence(start, increments)

        val emitted = flowOf(*seq.toTypedArray()).moment(3.seconds).toList()

        assertEquals(1, emitted.size)
        assertEquals(3.seconds, emitted.first().passedTime)
    }

    @Test
    @DisplayName("remaining は指定した残り時間の InProgress のみを流す")
    fun remainingMatchesExactRemainingTime() = runBlocking {
        val start = 10.seconds

        val increments = listOf(7.seconds)
        val seq = makeInProgressSequence(start, increments)

        val emitted = flowOf(*seq.toTypedArray()).remaining(3.seconds).toList()

        assertEquals(1, emitted.size)
        assertEquals(7.seconds, emitted.first().passedTime)
        assertEquals(3.seconds, emitted.first().remainingTime)
    }

    @Test
    @DisplayName("whenLaunched は Launched のみを流す")
    fun whenLaunchedFiltersLaunched() = runBlocking {
        val launched = mockk<ScheduleState.Active.Launched>()
        every { launched.startTime } returns 5.seconds

        val inProgress = mockk<ScheduleState.Active.InProgress>()
        val inProgress2 = mockk<ScheduleState.Active.InProgress>()

        val emitted: List<ScheduleState.Active.Launched> = flowOf(launched, inProgress, inProgress2)
            .whenLaunched()
            .toList()

        assertEquals(1, emitted.size)
        assertEquals(5.seconds, emitted.first().startTime)
    }

    @Test
    @DisplayName("whenOvertime は Overtime のみを流す")
    fun whenOvertimeFiltersOvertime() = runBlocking {
        val maybeOvertime = mockk<ScheduleState.Cancelled.Overtime>()
        every { maybeOvertime.startTime } returns 3.seconds

        val emitted = flowOf(maybeOvertime).whenOvertime().toList()

        assertEquals(1, emitted.size)
        assertEquals(3.seconds, emitted.first().startTime)
    }

    @Test
    @DisplayName("whenAborted は Aborted のみを流す")
    fun whenAbortedFiltersAborted() = runBlocking {
        val aborted = mockk<ScheduleState.Cancelled.Aborted>()
        every { aborted.startTime } returns 5.seconds

        val emitted = flowOf(aborted).whenAborted().toList()

        assertEquals(1, emitted.size)
        assertEquals(5.seconds, emitted.first().startTime)
    }
}
