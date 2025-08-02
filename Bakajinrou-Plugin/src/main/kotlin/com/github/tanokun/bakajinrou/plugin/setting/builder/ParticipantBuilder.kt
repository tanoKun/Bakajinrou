package com.github.tanokun.bakajinrou.plugin.setting.builder

import com.github.tanokun.bakajinrou.api.participant.GrantedStrategy
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.nonSpectators
import com.github.tanokun.bakajinrou.api.participant.position.citizen.IdiotPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.KnightPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.fox.FoxThirdPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.wolf.MadmanSecondPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.wolf.WolfSecondPosition
import java.util.*
import kotlin.random.Random

class ParticipantBuilder(
    private val positions: HashMap<Positions, Int>,
    candidates: Set<UUID>,
    private val strategy: (uuid: UUID) -> GrantedStrategy,
    val random: Random,
) {
    private val finalizedParticipants: MutableSet<Participant> = mutableSetOf()

    private val remainingCandidates: MutableSet<UUID> = candidates.toMutableSet()

    init {
        if (positions.values.sum() > candidates.size) throw IllegalStateException("現在の参加人数では、選択されている役職が多すぎます。")

    }

    fun assignMadmans(): WolfAssigner {
        val numberOf = positions[Positions.Madman] ?: 0

        val madmans = arrayListOf<Participant>()

        repeat(numberOf) {
            val pull = pullCandidate()

            madmans.add(Participant(pull, MadmanSecondPosition, strategy(pull)))
        }

        finalizedParticipants.addAll(madmans)

        return WolfAssignerImpl(madmans)
    }

    private inner class WolfAssignerImpl(private val madmans: List<Participant>): WolfAssigner {
        override fun assignWolfs(canDuplicate: Boolean): IdiotAssigner {
            val numberOf = positions[Positions.Wolf] ?: 0

            val associate = madmans.mapIndexed { index, madman ->
                if (index <= numberOf - 1) return@mapIndexed madman to index
                if (canDuplicate) return@mapIndexed madman to random.nextInt(numberOf)

                return@mapIndexed madman to -1
            }

            repeat(numberOf) {
                val pull = pullCandidate()
                val knownByMadmans = associate
                    .filter { (_, index) -> index == it }
                    .map { (madman, _) -> madman }
                    .nonSpectators()

                finalizedParticipants.add(Participant(pull, WolfSecondPosition(knownByMadmans), strategy(pull)))
            }

            return IdiotAssignerImpl()
        }
    }

    private inner class IdiotAssignerImpl: IdiotAssigner {
        override fun assignIdiots(vararg assign: IdiotPosition): AbilityUsersAssigner {
            if (assign.isEmpty()) throw IllegalArgumentException("振り分けるバカ役職が一つもありません。")

            val numberOf = positions[Positions.Idiot] ?: 0

            repeat(numberOf) {
                val pull = pullCandidate()
                finalizedParticipants.add(Participant(pull, assign.random(random), strategy(pull)))
            }

            return AbilityUsersAssignerImpl()
        }
    }

    private inner class AbilityUsersAssignerImpl: AbilityUsersAssigner {
        override fun assignAbilityUsers(): FoxAssigner {
            mapOf(
                FortunePosition to (positions[Positions.Fortune] ?: 0),
                MediumPosition to (positions[Positions.Medium] ?: 0),
                KnightPosition to (positions[Positions.Knight] ?: 0),
            ).forEach { position, numberOf ->
                repeat(numberOf) {
                    val pull = pullCandidate()
                    finalizedParticipants.add(Participant(pull, position, strategy(pull)))
                }
            }



            return FoxAssignerImpl()
        }
    }

    private inner class FoxAssignerImpl: FoxAssigner {
        override fun assignFox(): OtherAssigner {
            val numberOf = positions[Positions.Fox] ?: 0

            repeat(numberOf) {
                val pull = pullCandidate()

                finalizedParticipants.add(Participant(pull, FoxThirdPosition, strategy(pull)))
            }

            return OtherAssignerImpl()
        }
    }

    private inner class OtherAssignerImpl: OtherAssigner {
        override fun assignOtherToCitizens(): ParticipantScope.NonSpectators {
            remainingCandidates.forEach {
                finalizedParticipants.add(Participant(it, CitizenPosition, strategy(it)))
            }

            return finalizedParticipants.toList().nonSpectators()
        }
    }


    private fun pullCandidate(): UUID {
        val pull = remainingCandidates.random(random)
        remainingCandidates.remove(pull)

        return pull
    }

}


interface WolfAssigner {
    /**
     * @param canDuplicate 人狼とそれを知る狂人が、1対多であることを容認します。
     */
    fun assignWolfs(canDuplicate: Boolean): IdiotAssigner
}

interface IdiotAssigner {
    fun assignIdiots(vararg assign: IdiotPosition): AbilityUsersAssigner
}

interface AbilityUsersAssigner {
    fun assignAbilityUsers(): FoxAssigner
}

interface FoxAssigner {
    fun assignFox(): OtherAssigner
}

interface OtherAssigner {
    fun assignOtherToCitizens(): ParticipantScope.NonSpectators
}