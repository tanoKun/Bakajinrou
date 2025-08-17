package com.github.tanokun.bakajinrou.plugin.common.setting.builder

import com.github.tanokun.bakajinrou.api.participant.*
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.FortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.KnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.MediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import java.util.*
import kotlin.random.Random

class ParticipantBuilder(
    private val template: HashMap<RequestedPositions, Int>,
    candidates: Set<UUID>,
    private val random: Random,
) {
    private val candidates = candidates.map { it.asParticipantId() }.toSet()

    init {
        if (template.values.sum() > candidates.size) throw IllegalStateException("現在の参加人数では、選択されている役職が多すぎます。")

    }

    class MadmanAssigner private constructor(
        internal val template: Map<RequestedPositions, Int>,
        internal val random: Random,
        internal val madmans: Set<Participant>,
        internal val remainingCandidates: Set<ParticipantId>,
    ) {
        companion object {
            fun ParticipantBuilder.assignMadmans(): MadmanAssigner {
                val amount = template[RequestedPositions.MADMAN] ?: 0

                val pull = candidates.shuffled(random).take(amount)

                val madmans = pull.map {
                    val hasFakeProtectAbility = random.nextInt(2) == 0
                    Participant(it, MadmanPosition(hasFakeProtectAbility), GrantedStrategy(mapOf()))
                }

                return MadmanAssigner(template, random, madmans.toSet(), candidates - pull)
            }
        }
    }

    class WolfAssigner private constructor(
        internal val template: Map<RequestedPositions, Int>,
        internal val random: Random,
        internal val assignedParticipants: Set<Participant>,
        internal val remainingCandidates: Set<ParticipantId>,
    ) {
        companion object {
            fun MadmanAssigner.assignWolfs(canDuplicate: Boolean): WolfAssigner {
                val amount = template[RequestedPositions.WOLF] ?: 0

                val pull = remainingCandidates.shuffled(random).take(amount)

                val associate = madmans.mapIndexed { index, madman ->
                    if (index <= amount - 1) return@mapIndexed madman to index
                    if (canDuplicate) return@mapIndexed madman to random.nextInt(amount)

                    return@mapIndexed madman to -1
                }

                val wolfs = pull.mapIndexed { pullIndex, uniqueId ->
                    val knownByMadmans = associate
                        .filter { (_, index) -> index == pullIndex }
                        .map { (madman, _) -> madman }
                        .excludeSpectators()

                    Participant(uniqueId, WolfPosition(knownByMadmans), GrantedStrategy(mapOf()))
                }

                return WolfAssigner(template, random, madmans + wolfs, remainingCandidates - pull)
            }
        }
    }

    class IdiotAssigner private constructor(
        internal val template: Map<RequestedPositions, Int>,
        internal val random: Random,
        internal val assignedParticipants: Set<Participant>,
        internal val remainingCandidates: Set<ParticipantId>,
    ) {
        companion object {
            fun WolfAssigner.assignIdiots(vararg assign: IdiotPosition): IdiotAssigner {

                val amount = template[RequestedPositions.IDIOT] ?: 0

                if (assign.isEmpty() && amount > 0) throw IllegalArgumentException("振り分けるバカ役職が一つもありません。")

                val pull = remainingCandidates.shuffled(random).take(amount)

                val idiots = pull.map {
                    Participant(it, assign.random(random), GrantedStrategy(mapOf()))
                }

                return IdiotAssigner(template, random, assignedParticipants + idiots, remainingCandidates - pull)
            }
        }
    }

    class AbilityUsersAssigner private constructor(
        internal val template: Map<RequestedPositions, Int>,
        internal val random: Random,
        internal val assignedParticipants: Set<Participant>,
        internal val remainingCandidates: Set<ParticipantId>,
    ) {
        companion object {
            fun IdiotAssigner.assignAbilityUsers(): AbilityUsersAssigner {
                val positions = listOf(
                    List(template[RequestedPositions.FORTUNE] ?: 0) { FortunePosition },
                    List(template[RequestedPositions.MEDIUM] ?: 0) { MediumPosition },
                    List(template[RequestedPositions.KNIGHT] ?: 0) { KnightPosition },
                ).flatten()

                val pull = remainingCandidates.shuffled(random).take(positions.size)

                val assigned = pull.mapIndexed { index, uniqueId ->
                    Participant(uniqueId, positions[index], GrantedStrategy(mapOf()))
                }

                return AbilityUsersAssigner(template, random, assignedParticipants + assigned, remainingCandidates - pull)
            }
        }
    }

    class FoxAssigner private constructor(
        internal val assignedParticipants: Set<Participant>,
        internal val remainingCandidates: Set<ParticipantId>,
    ) {
        companion object {
            fun AbilityUsersAssigner.assignFox(): FoxAssigner {
                val amount = template[RequestedPositions.FOX] ?: 0

                val pull = remainingCandidates.shuffled(random).take(amount)

                val fox = pull.map {
                    Participant(it, FoxPosition, GrantedStrategy(mapOf()))
                }

                return FoxAssigner(assignedParticipants + fox, remainingCandidates - pull)
            }
        }
    }

    class CitizenAssigner private constructor() {
        companion object {
            fun FoxAssigner.assignCitizens(): ParticipantScope.NonSpectators {
                val pull = remainingCandidates

                val citizens = pull.map {
                    Participant(it, CitizenPosition, GrantedStrategy(mapOf()))
                }

                return (assignedParticipants + citizens).excludeSpectators()
            }
        }
    }
}