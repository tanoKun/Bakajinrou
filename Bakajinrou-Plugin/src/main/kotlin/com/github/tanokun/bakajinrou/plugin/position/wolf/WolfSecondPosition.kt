package com.github.tanokun.bakajinrou.plugin.position.wolf

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import java.util.*

object WolfSecondPosition: WolfPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "人狼", defaultPrefix = "人狼")

    override fun doAtStarting(uniqueId: UUID) {}

    override fun isVisibleBy(viewer: Participant): Boolean =
        super.isVisibleBy(viewer) || viewer.isPosition<WolfPosition>()
}