package com.github.tanokun.bakajinrou.api.map

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PointLocationTest {
    @Test
    fun serializeTest() {
        val point = PointLocation("testWorld", 1, 100, 10)
        val encode = Json.encodeToString(point)

        assertEquals("{\"worldName\":\"testWorld\",\"x\":1,\"y\":100,\"z\":10}", encode)

        val decode = Json.decodeFromString<PointLocation>(encode)
        assertEquals(point, decode)

    }

}