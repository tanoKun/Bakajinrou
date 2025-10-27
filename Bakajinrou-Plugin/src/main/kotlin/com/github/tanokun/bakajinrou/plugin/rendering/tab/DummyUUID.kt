package com.github.tanokun.bakajinrou.plugin.rendering.tab

import java.util.*

data class DummyUUID(val uuid: UUID) {
    companion object {
        fun random(): DummyUUID = DummyUUID(UUID.randomUUID())
    }
}