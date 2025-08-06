package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope.NonSpectators
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition

sealed class ParticipantScope(participant: Set<Participant>): Set<Participant> by participant {
    class All(participant: Set<Participant>): ParticipantScope(participant) {
        fun excludeSpectators(): NonSpectators = NonSpectators(this)

        fun includes(filter: ParticipantFilter) = All(this.filter(filter).toSet())

        fun excludes(filter: ParticipantFilter) = All(this.filterNot(filter).toSet())

        fun survivedOnly() = NonSpectators(this.filter { it.state == ParticipantStates.ALIVE }.toSet())
    }

    class NonSpectators(participant: Set<Participant>) : ParticipantScope(participant.filterNot { it.isPosition<SpectatorPosition>() }.toSet()) {
        fun includes(filter: ParticipantFilter) = NonSpectators(this.filter(filter).toSet())

        fun excludes(filter: ParticipantFilter) = NonSpectators(this.filterNot(filter).toSet())
    }
}

typealias ParticipantFilter = ((Participant) -> Boolean)

infix fun ParticipantFilter.or(other: ParticipantFilter): ParticipantFilter = { t ->
    this(t) || other(t)
}

infix fun ParticipantFilter.and(other: ParticipantFilter): ParticipantFilter = { t ->
    this(t) && other(t)
}

fun Iterable<Participant>.all() = ParticipantScope.All(this.toSet())

fun Iterable<Participant>.excludeSpectators() = NonSpectators(this.toSet())