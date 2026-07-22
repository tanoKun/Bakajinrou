package com.github.tanokun.bakajinrou.plugin.map.gimmick.lifecycle

import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId

interface MapGimmick : AutoCloseable {
    val id: MapGimmickId

    fun start()
}
