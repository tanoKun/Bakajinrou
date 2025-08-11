package com.github.tanokun.bakajinrou.api.participant

import java.util.*

data class ParticipantId(val uniqueId: UUID)

fun UUID.asParticipantId() = ParticipantId(this)
