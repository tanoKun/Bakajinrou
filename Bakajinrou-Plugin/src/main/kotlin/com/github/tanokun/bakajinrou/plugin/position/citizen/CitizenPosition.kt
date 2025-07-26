package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import java.util.*

object CitizenPosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "市民", defaultPrefix = "市民")

    override val publicPosition: Position = this

    override fun doAtStarting(uniqueId: UUID) {}
}