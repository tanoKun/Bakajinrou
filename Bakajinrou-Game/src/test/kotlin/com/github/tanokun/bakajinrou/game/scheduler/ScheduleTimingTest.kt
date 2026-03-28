package com.github.tanokun.bakajinrou.game.scheduler

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ScheduleTimingTest {

    private fun makeInProgressSequence(start: Duration, increments: List<Duration>): List<ScheduleState.Active.InProgress> {
        val list = mutableListOf<ScheduleState.Active.InProgress>()
        var state: ScheduleState = ScheduleState.Pending(start).launch()
        for (inc in increments) {
            state = (state as ScheduleState.Active).advance(inc)
            if (state is ScheduleState.Active.InProgress) list.add(state)
            else break
        }
        return list
    }

    @Test
    @DisplayName("every は指定間隔ごとの最初の InProgress を流す")
    fun everyEmitsOnInterval() = runBlocking {
        val start = 10.seconds
        // passedTime: 1,2,3,4,5 秒の InProgress を作る
        val increments = listOf(1.seconds, 1.seconds, 1.seconds, 1.seconds, 1.seconds)
        val seq = makeInProgressSequence(start, increments)

        val emitted = flowOf(*seq.toTypedArray()).every(2.seconds).toList()

        // ceil(passedTime / 2): 1->1,2->1,3->2,4->2,5->3 なので emitted は passedTime 1,3,5 が期待
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

        // 3秒の passedTime を持つ要素が1つある
        assertEquals(1, emitted.size)
        assertEquals(3.seconds, emitted.first().passedTime)
    }

    @Test
    @DisplayName("remaining は指定した残り時間の InProgress のみを流す")
    fun remainingMatchesExactRemainingTime() = runBlocking {
        val start = 10.seconds

        // 7 秒経過すると remaining = 3 秒
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
        val launched = ScheduleState.Pending(5.seconds).launch()
        val inProgress = launched.advance(1.seconds) as ScheduleState.Active.InProgress
        val inProgress2 = inProgress.advance(1.seconds)

        val emitted: List<ScheduleState.Active.Launched> = flowOf(launched, inProgress, inProgress2)
            .whenLaunched()
            .toList()

        assertEquals(1, emitted.size)
        assertEquals(5.seconds, emitted.first().startTime)
    }

    @Test
    @DisplayName("whenOvertime は Overtime のみを流す")
    fun whenOvertimeFiltersOvertime() = runBlocking {
        val launched = ScheduleState.Pending(3.seconds).launch()
        // advance により残り時間を超過させると Overtime になる
        val maybeOvertime = launched.advance(4.seconds)

        val emitted = flowOf(maybeOvertime).whenOvertime().toList()

        assertEquals(1, emitted.size)
        assertEquals(3.seconds, emitted.first().startTime)
    }

    @Test
    @DisplayName("whenAborted は Aborted のみを流す")
    fun whenAbortedFiltersAborted() = runBlocking {
        val launched = ScheduleState.Pending(5.seconds).launch()
        val aborted = launched.abort()

        val emitted = flowOf(aborted).whenAborted().toList()

        assertEquals(1, emitted.size)
        assertEquals(5.seconds, emitted.first().startTime)
    }
}

