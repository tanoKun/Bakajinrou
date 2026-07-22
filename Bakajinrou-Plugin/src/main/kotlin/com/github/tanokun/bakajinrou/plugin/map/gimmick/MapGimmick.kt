package com.github.tanokun.bakajinrou.plugin.map.gimmick

interface MapGimmick : AutoCloseable {
    val id: MapGimmickId

    fun start()
}
