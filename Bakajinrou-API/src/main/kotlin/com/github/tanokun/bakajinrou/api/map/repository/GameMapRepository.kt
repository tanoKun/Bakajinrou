package com.github.tanokun.bakajinrou.api.map.repository

import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.map.MapName
import kotlinx.coroutines.flow.Flow

interface GameMapRepository {
    suspend fun save(gameMap: GameMap)

    suspend fun delete(gameMap: GameMap)

    suspend fun loadBy(mapName: MapName): GameMap?

    fun loadAll(): Flow<GameMap>
}