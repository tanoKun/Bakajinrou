package com.github.tanokun.bakajinrou.plugin.map

import kotlinx.serialization.Serializable
import org.bukkit.Material
import kotlin.time.Duration

@Serializable
data class GameMap(
    val mapName: MapName,
    val spawnPoint: PointLocation,
    val lobbyPoint: PointLocation,
    val startTime: Duration,
    val icon: Material
)