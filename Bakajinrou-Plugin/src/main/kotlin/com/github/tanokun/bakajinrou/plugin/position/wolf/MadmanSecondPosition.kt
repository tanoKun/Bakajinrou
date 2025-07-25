package com.github.tanokun.bakajinrou.plugin.position.wolf

import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import java.util.*

object MadmanSecondPosition: MadmanPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "狂人", defaultPrefix = "狂人")

    override fun doAtStarting(uniqueId: UUID) {}
}