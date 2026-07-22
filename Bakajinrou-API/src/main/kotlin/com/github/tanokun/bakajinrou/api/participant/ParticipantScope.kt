package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates

sealed class ParticipantScope(participant: Set<Participant>): Set<Participant> by participant {
    class All(participant: Set<Participant>): ParticipantScope(participant) {
        fun includes(filter: ParticipantFilter) = All(this.filter(filter).toSet())

        fun excludes(filter: ParticipantFilter) = All(this.filterNot(filter).toSet())

        fun excludes(participantId: ParticipantId) = All(this.filterNot { it.participantId == participantId }.toSet())

        fun survivedOnly() = All(this.filter { it.state == ParticipantStates.ALIVE }.toSet())
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
