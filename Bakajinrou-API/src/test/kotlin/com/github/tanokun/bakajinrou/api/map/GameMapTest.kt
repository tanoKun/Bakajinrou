package com.github.tanokun.bakajinrou.api.map

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes

class GameMapTest {
    private val point1 = PointLocation("testWorld", 1, 1, 1)
    private val point2 = PointLocation("testWorld2", 2, 2, 2)

    @Test
    fun serializeTest() {
        val gameMap = GameMap(MapName("testMap"), point1, point2, 15.minutes)
        val encode = Json.encodeToString(gameMap)

        assertEquals(
            "{\"mapName\":{\"name\":\"testMap\"},\"spawnPoint\":{\"worldName\":\"testWorld\",\"x\":1,\"y\":1,\"z\":1},\"lobbyPoint\":{\"worldName\":\"testWorld2\",\"x\":2,\"y\":2,\"z\":2},\"startTime\":\"PT15M\"}",
            encode
        )

        val decode = Json.decodeFromString<GameMap>(encode)
        assertEquals(gameMap, decode)

    }
}

