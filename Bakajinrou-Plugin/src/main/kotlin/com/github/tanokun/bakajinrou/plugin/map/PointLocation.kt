package com.github.tanokun.bakajinrou.plugin.map

import kotlinx.serialization.Serializable

@Serializable
data class PointLocation(val worldName: String, val x: Int, val y: Int, val z: Int)