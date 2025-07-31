package com.github.tanokun.bakajinrou.plugin.cache

import com.destroystokyo.paper.profile.PlayerProfile
import java.util.*

object PlayerSkinCache {
    private val cache: HashMap<UUID, String> = hashMapOf()

    fun put(uuid: UUID, profile: PlayerProfile) {
        val textures = profile.properties.first { it.name == "textures" }
        cache[uuid] = textures.value
    }

    fun getTexture(uuid: UUID) = cache[uuid]
}