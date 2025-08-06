package com.github.tanokun.bakajinrou.game.scheduler

import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState.Active
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface ScheduleState {
    val startTime: Duration

    data class Pending(override val startTime: Duration) : ScheduleState {
        fun launch(): Active.Launched = LaunchedImpl(startTime)
    }

    sealed interface Active {
        interface Launched: ScheduleState, Active
        interface InProgress: ScheduleState, Active { val passedTime: Duration }

        fun advance(time: Duration): ScheduleState

        fun abort(): Cancelled.Aborted
    }

    sealed interface Cancelled {
        interface Overtime: ScheduleState, Cancelled
        interface Aborted: ScheduleState, Cancelled
    }
}

private abstract class ActiveImpl(override val startTime: Duration, private val passedTime: Duration) : Active.Launched {
    override fun advance(time: Duration): ScheduleState {
        val remainTime = startTime - passedTime
        val nextState =
            if (remainTime < time) OverTimeImpl(startTime)
            else InProgressImpl(startTime, passedTime + time)

        return nextState
    }

    override fun abort() = AbortedImpl(startTime)
}

private data class LaunchedImpl(override val startTime: Duration) : ActiveImpl(startTime, 0.seconds)
private data class InProgressImpl(
    override val startTime: Duration, override val passedTime: Duration
) : Active.InProgress, ActiveImpl(startTime, passedTime)

private data class OverTimeImpl(override val startTime: Duration): ScheduleState.Cancelled.Overtime
private data class AbortedImpl(override val startTime: Duration): ScheduleState.Cancelled.Aborted