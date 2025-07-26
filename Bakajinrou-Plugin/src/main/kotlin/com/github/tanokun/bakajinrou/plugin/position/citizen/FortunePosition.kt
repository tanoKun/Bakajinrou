package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import java.util.*

object FortunePosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "占い師", defaultPrefix = "占い師")

    override val publicPosition: Position = CitizenPosition

    override fun doAtStarting(uniqueId: UUID) {}
}