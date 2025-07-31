package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition

sealed class ParticipantScope(participant: List<Participant>): List<Participant> by participant {
    class All(participant: List<Participant>): ParticipantScope(participant) {
        fun nonSpectators(): NonSpectators = NonSpectators(this)

        fun survivedOnly(): NonSpectators = NonSpectators(this.filter { it.state == ParticipantStates.SURVIVED })

        inline fun <reified P: Position> position() = All(this.filter { it.isPosition<P>() })

        inline fun <reified P: Position> excludePosition() = All(this.filterNot { it.isPosition<P>() })

        fun exclude(participant: Participant) = All(this.filterNot { it == participant })
    }


    class NonSpectators(participant: List<Participant>) : ParticipantScope(participant.filterNot { it.isPosition<SpectatorPosition>() }) {
        fun survivedOnly(): NonSpectators = NonSpectators(this.filter { it.state == ParticipantStates.SURVIVED })

        inline fun <reified P: Position> position() = NonSpectators(this.filter { it.isPosition<P>() })

        inline fun <reified P: Position> excludePosition() = NonSpectators(this.filterNot { it.isPosition<P>() })

        fun exclude(participant: Participant) = NonSpectators(this.filterNot { it == participant })
    }
}

fun List<Participant>.all() = ParticipantScope.All(this)
fun List<Participant>.nonSpectators() = ParticipantScope.NonSpectators(this)