package com.github.tanokun.bakajinrou.game.map

import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.map.MapName
import com.github.tanokun.bakajinrou.api.map.repository.GameMapRepository
import com.github.tanokun.bakajinrou.game.map.result.MapCreationResult
import com.github.tanokun.bakajinrou.game.map.result.MapDeletionResult
import com.github.tanokun.bakajinrou.game.map.result.MapUpdateResult
import java.util.concurrent.ConcurrentHashMap

class GameMapRegistry(
    private val repository: GameMapRepository
) {
    private val maps = ConcurrentHashMap<MapName, GameMap>()

    suspend fun loadAllMapsFromRepository() {
        repository.loadAll().collect {
            maps[it.mapName] = it
        }
    }

    fun findBy(name: MapName): GameMap? = maps[name]

    fun findAll(): List<GameMap> = maps.values.toList()

    suspend fun create(gameMap: GameMap): MapCreationResult {
        repository.loadBy(gameMap.mapName)?.let {
            return MapCreationResult.MapAlreadyExists(it)
        }

        maps[gameMap.mapName] = gameMap
        repository.save(gameMap)

        return MapCreationResult.CreationSucceeded
    }

    suspend fun update(gameMap: GameMap): MapUpdateResult {
        repository.loadBy(gameMap.mapName) ?: let {
            return MapUpdateResult.MapNotFound
        }

        maps[gameMap.mapName] = gameMap
        repository.save(gameMap)

        return MapUpdateResult.UpdateSucceeded
    }

    suspend fun deleteBy(name: MapName): MapDeletionResult {
        val map = maps.remove(name) ?: return MapDeletionResult.MapNotFound
        repository.delete(map)

        return MapDeletionResult.DeletionSucceeded(map)
    }
}