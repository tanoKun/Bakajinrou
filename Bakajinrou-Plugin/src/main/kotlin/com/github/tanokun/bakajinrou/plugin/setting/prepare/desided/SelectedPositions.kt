package com.github.tanokun.bakajinrou.plugin.setting.prepare.desided

import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import kotlinx.serialization.Serializable

@Serializable
data class SelectedPositions(val positions: Map<RequestedPositions, Int>){
    init {
        require(positions.isNotEmpty()) { "選択されている役職がありません" }
        positions.forEach {
            require(it.value >= 0) { "全ての役職の人数は、0人以上である必要があります" }
        }
    }

    fun totalCount(): Int = positions.values.sum()
}