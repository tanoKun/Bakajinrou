package com.github.tanokun.bakajinrou.game.cache

import java.util.*

class PlayerNameCache {
    private val caches = hashMapOf<UUID, String>()

    fun put(uniqueId: UUID, name: String) {
        caches[uniqueId] = name
    }

    fun get(uniqueId: UUID): String? = caches[uniqueId]
}