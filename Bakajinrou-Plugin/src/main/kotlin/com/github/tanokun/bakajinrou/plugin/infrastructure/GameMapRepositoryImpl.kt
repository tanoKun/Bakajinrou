package com.github.tanokun.bakajinrou.plugin.infrastructure

import com.github.tanokun.bakajinrou.plugin.map.GameMap
import com.github.tanokun.bakajinrou.plugin.map.MapName
import com.github.tanokun.bakajinrou.plugin.map.repository.GameMapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.io.File

class GameMapRepositoryImpl(private val folder: File): GameMapRepository {
    private val mutex = Mutex()

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    init {
        if (!folder.exists()) folder.mkdirs()
    }

    override suspend fun save(gameMap: GameMap) {
        mutex.withLock {
            val file = File(folder, "${gameMap.mapName.name}.json")

            if (!folder.exists()) folder.mkdirs()

            file.writeText(json.encodeToString(gameMap))
        }
    }

    override suspend fun delete(gameMap: GameMap) {
        mutex.withLock {
            val file = File(folder, "${gameMap.mapName.name}.json")

            if (!file.exists()) throw IllegalStateException("存在しないマップデータです。 (name: ${gameMap.mapName.name})")

            file.delete()
        }
    }

    override suspend fun loadBy(mapName: MapName): GameMap? {
        mutex.withLock {
            val file = File(folder, "${mapName.name}.json")

            if (!file.exists()) return null

            return loadByFile(file)
        }
    }

    override fun loadAll(): Flow<GameMap> = channelFlow {
        val files = folder.listFiles { it.extension == "json" } ?: return@channelFlow

        for (file in files) {
            launch(Dispatchers.IO) {
                send(loadByFile(file))
            }
        }
    }

    private fun loadByFile(file: File): GameMap {
        val text = file.readText()
        val gameMap = Json.decodeFromString<GameMap>(text)

        return gameMap
    }
}