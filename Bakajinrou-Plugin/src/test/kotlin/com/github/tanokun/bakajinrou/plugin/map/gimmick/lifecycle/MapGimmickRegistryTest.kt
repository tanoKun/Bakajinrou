package com.github.tanokun.bakajinrou.plugin.map.gimmick.lifecycle

import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class MapGimmickRegistryTest {
    @Test
    fun findsGimmickById() {
        val hijack = FakeMapGimmick(MapGimmickId.HIJACK)
        val registry = MapGimmickRegistry(listOf(hijack))

        assertSame(hijack, registry.findBy(MapGimmickId.HIJACK))
        assertNull(registry.findBy(MapGimmickId.NONE))
    }

    private class FakeMapGimmick(override val id: MapGimmickId) : MapGimmick {
        override fun start() = Unit
        override fun close() = Unit
    }
}
