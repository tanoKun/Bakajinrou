package com.github.tanokun.bakajinrou.plugin.setting.template

import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
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
            val roles = listOf(
                Positions.Wolf.displayName to wolf,
                Positions.Madman.displayName to madman,
                Positions.Fox.displayName to fox,
                Positions.Idiot.displayName to idiot,
                Positions.Fortune.displayName to fortune,
                Positions.Medium.displayName to medium,
                Positions.Knight.displayName to knight
            )

            roles.forEach { (name, count) ->
                require(count >= 0) { "$name の人数は 0 以上である必要があります（現在: $count）" }
            }
        }

        fun getPositions(): Map<Positions, Int> =
            hashMapOf(
                Positions.Wolf to wolf,
                Positions.Madman to madman,
                Positions.Idiot to idiot,
                Positions.Fortune to fortune,
                Positions.Medium to medium,
                Positions.Knight to knight,
                Positions.Fox to fox
            )
    }

    fun getPositions(amount: Int): Map<Positions, Int>? = templates[amount]?.getPositions()
}