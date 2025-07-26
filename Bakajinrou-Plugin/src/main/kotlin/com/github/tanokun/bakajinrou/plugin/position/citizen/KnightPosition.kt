package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import java.util.*

object KnightPosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "騎士", defaultPrefix = "騎士")

    override fun doAtStarting(uniqueId: UUID) {}
}