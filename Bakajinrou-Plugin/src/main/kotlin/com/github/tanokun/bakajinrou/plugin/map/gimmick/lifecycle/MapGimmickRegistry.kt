package com.github.tanokun.bakajinrou.plugin.map.gimmick.lifecycle

import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId

class MapGimmickRegistry(gimmicks: Collection<MapGimmick>) {
    private val gimmicksById = gimmicks.associateBy(MapGimmick::id)

    fun findBy(id: MapGimmickId): MapGimmick? = gimmicksById[id]
}
