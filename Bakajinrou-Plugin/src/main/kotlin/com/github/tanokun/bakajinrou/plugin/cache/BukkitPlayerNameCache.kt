package com.github.tanokun.bakajinrou.plugin.cache

import org.bukkit.entity.Player
import java.util.*

class BukkitPlayerNameCache {
    private val caches = hashMapOf<UUID, String>()

    fun put(bukkitPlayer: Player) = put(bukkitPlayer.uniqueId, bukkitPlayer.name)

    fun put(uniqueId: UUID, name: String) {
        caches[uniqueId] = name
    }

    fun remove(bukkitPlayer: Player) {
        caches.remove(bukkitPlayer.uniqueId)
    }

    fun get(bukkitPlayer: Player): String? = get(bukkitPlayer.uniqueId)

    fun get(uniqueId: UUID): String? = caches[uniqueId]
}