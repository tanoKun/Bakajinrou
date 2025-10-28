package com.github.tanokun.bakajinrou.plugin.rendering.tab.gaming

import com.github.tanokun.bakajinrou.plugin.rendering.tab.DummyUUID
import org.koin.core.annotation.Single
import java.util.*

@Single(createdAtStart = true)
class DummyPlayers {
    private val dummyUuids = hashMapOf<UUID, DummyUUID>()

    fun getDummyUuidOrPut(original: UUID): DummyUUID = dummyUuids.getOrPut(original) { DummyUUID.random() }
}