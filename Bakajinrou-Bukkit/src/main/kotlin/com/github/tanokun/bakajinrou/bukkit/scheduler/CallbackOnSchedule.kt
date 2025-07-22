package com.github.tanokun.bakajinrou.bukkit.scheduler

fun interface CallbackOnSchedule {
    operator fun invoke(leftSeconds: Long)
}