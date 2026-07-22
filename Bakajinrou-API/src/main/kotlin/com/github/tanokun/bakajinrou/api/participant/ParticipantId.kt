package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.player.PlayerId
import java.util.UUID

data class ParticipantId(val playerId: PlayerId) {
    constructor(uniqueId: UUID): this(PlayerId(uniqueId))

    val uniqueId: UUID get() = playerId.uniqueId
}

fun UUID.asParticipantId() = ParticipantId(this)
