package com.github.tanokun.bakajinrou.game.scheduler

fun interface CallbackOnSchedule {
    operator fun invoke(leftSeconds: Long)
}