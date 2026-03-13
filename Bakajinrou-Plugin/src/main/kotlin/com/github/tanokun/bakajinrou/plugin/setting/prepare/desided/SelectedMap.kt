package com.github.tanokun.bakajinrou.plugin.setting.prepare.desided

import com.github.tanokun.bakajinrou.plugin.map.GameMap
import kotlinx.serialization.Serializable

@Serializable
data class SelectedMap(val map: GameMap)
