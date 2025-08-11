package com.github.tanokun.bakajinrou.plugin.common.setting.template

import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import kotlinx.serialization.Serializable

@Serializable
class DistributionTemplates(private val templates: HashMap<Int, DistributionTemplate>) {

    @Serializable
    data class DistributionTemplate(
        val wolf: Int,
        val madman: Int,
        val idiot: Int,
        val fortune: Int,
        val medium: Int,
        val knight: Int,
        val fox: Int,
    ) {
        init {
            val roles = getPositions()

            roles.forEach { (position, count) ->
                require(count >= 0) { "${position.name} の人数は 0 以上である必要があります（現在: $count）" }
            }
        }

        fun getPositions(): Map<RequestedPositions, Int> =
            hashMapOf(
                RequestedPositions.WOLF to wolf,
                RequestedPositions.MADMAN to madman,
                RequestedPositions.IDIOT to idiot,
                RequestedPositions.FORTUNE to fortune,
                RequestedPositions.MEDIUM to medium,
                RequestedPositions.KNIGHT to knight,
                RequestedPositions.FOX to fox
            )
    }

    fun getPositions(amount: Int): Map<RequestedPositions, Int>? = templates[amount]?.getPositions()
}