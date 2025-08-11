package com.github.tanokun.bakajinrou.api.method

import java.util.*

data class MethodId(val uniqueId: UUID)

fun UUID.asMethodId() = MethodId(this)
