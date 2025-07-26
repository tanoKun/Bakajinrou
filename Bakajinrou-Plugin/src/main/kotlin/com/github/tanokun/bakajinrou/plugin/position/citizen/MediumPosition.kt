package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import java.util.*

object MediumPosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "霊媒師", defaultPrefix = "霊媒師")

    override fun doAtStarting(uniqueId: UUID) {}
}