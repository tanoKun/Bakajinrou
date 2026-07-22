package com.github.tanokun.bakajinrou.plugin.map

import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId
import kotlinx.serialization.Serializable
import org.bukkit.Material
import kotlin.time.Duration

@Serializable
data class GameMap(
    val mapName: MapName,
    val spawnPoint: PointLocation,
    val lobbyPoint: PointLocation,
    val startTime: Duration,
    val icon: Material,
    val gimmickId: MapGimmickId = MapGimmickId.NONE,
)
