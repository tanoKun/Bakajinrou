package com.github.tanokun.bakajinrou.api.participant.position.wolf

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position

interface WolfPosition: Position {
    fun getKnownBy(): ParticipantScope.NonSpectators
}