package com.github.tanokun.bakajinrou.plugin.setting.factory

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.StrategyIntegrity
import com.github.tanokun.bakajinrou.plugin.formatter.Positions
import com.github.tanokun.bakajinrou.plugin.participant.ParticipantStrategy
import com.github.tanokun.bakajinrou.plugin.position.SpectatorOtherPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition
import org.bukkit.entity.Player
import kotlin.random.Random

/**
 * プレイヤーの役職割り当てを行います。
 *
 * @property random 役職シャッフルに用いる乱数
 */
class PositionAssigner(
    private val random: Random
) {
    val amountOfPosition =
        hashMapOf<Positions, Int>(
            Positions.Wolf to 1,
            Positions.Madman to 0,
            Positions.Fox to 0,
            Positions.Idiot to 0,
            Positions.Fortune to 0,
            Positions.Medium to 0,
            Positions.Knight to 0
        )

    fun getMinimumRequired(): Int = amountOfPosition.values.sum()

    fun assignPositions(candidates: Set<Player>, spectators: Set<Player>, strategyIntegrity: StrategyIntegrity): List<Participant> = arrayListOf<Participant>().apply {
        addAll(spectators.map {
            Participant(
                it.uniqueId, SpectatorOtherPosition,
                ParticipantStrategy(it.uniqueId, strategyIntegrity)
            )
        })

        addAll(selectPositions(candidates, strategyIntegrity))
    }

    private fun selectPositions(candidates: Set<Player>, strategyIntegrity: StrategyIntegrity): List<Participant> {
        val participants = arrayListOf<Participant>()
        val shuffled = candidates.shuffled(random)

        var index = 0
        amountOfPosition.forEach { positionType, required ->
            repeat(required) {
                val position = positionType.candidatePositions.random(random)

                val uniqueId = shuffled[index].uniqueId

                participants.add(Participant(uniqueId, position, ParticipantStrategy(uniqueId, strategyIntegrity)))

                index++
            }
        }

        for (index in index..shuffled.lastIndex) {
            val uniqueId = shuffled[index].uniqueId

            participants.add(Participant(uniqueId, CitizenPosition, ParticipantStrategy(uniqueId, strategyIntegrity)))
        }

        return participants
    }

}