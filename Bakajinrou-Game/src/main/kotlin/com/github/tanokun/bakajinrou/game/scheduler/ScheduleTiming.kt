package com.github.tanokun.bakajinrou.game.scheduler

import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState.Active.InProgress
import kotlinx.coroutines.flow.*
import kotlin.math.ceil
import kotlin.time.Duration

fun Flow<ScheduleState>.every(time: Duration): Flow<InProgress> =
    this.filterIsInstance<InProgress>()
        .map { inProgress -> inProgress to ceil(inProgress.passedTime / time) }
        .distinctUntilChangedBy { it.second }
        .map { it.first }

fun Flow<ScheduleState>.moment(time: Duration): Flow<InProgress> =
    this.filterIsInstance<InProgress>()
        .filter { inProgress -> inProgress.passedTime == time }

fun Flow<ScheduleState>.remaining(time: Duration): Flow<InProgress> =
    this.filterIsInstance<InProgress>()
        .filter { inProgress -> inProgress.remainingTime == time }

fun Flow<ScheduleState>.whenLaunched(): Flow<ScheduleState.Active.Launched> =
    this.filterIsInstance()

fun Flow<ScheduleState>.whenOvertime(): Flow<ScheduleState.Cancelled.Overtime> =
    this.filterIsInstance()

fun Flow<ScheduleState>.whenAborted(): Flow<ScheduleState.Cancelled.Aborted> =
    this.filterIsInstance()