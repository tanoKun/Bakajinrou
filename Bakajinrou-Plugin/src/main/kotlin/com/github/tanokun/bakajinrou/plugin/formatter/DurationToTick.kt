package com.github.tanokun.bakajinrou.plugin.formatter

import kotlin.time.Duration

fun Duration.toTick(): Int = (this.inWholeSeconds * 20).toInt()