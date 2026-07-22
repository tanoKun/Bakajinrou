package com.github.tanokun.bakajinrou.plugin.presentation.tab

import java.util.*

data class DummyUUID(val uuid: UUID) {
    companion object {
        fun random(): DummyUUID = DummyUUID(UUID.randomUUID())
    }
}