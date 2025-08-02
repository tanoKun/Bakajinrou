package com.github.tanokun.bakajinrou.api.map

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class GameMap(
    val mapName: MapName,
    val spawnPoint: PointLocation,
    val lobbyPoint: PointLocation,
    val startTime: Duration,
    val delayToGiveQuartz: Duration
)