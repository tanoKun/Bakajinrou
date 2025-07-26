package com.github.tanokun.bakajinrou.plugin.position

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import java.util.*

object SpectatorOtherPosition: SpectatorPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "観戦", defaultPrefix = "観戦")

    override val publicPosition: Position = this

    override fun doAtStarting(uniqueId: UUID) {}

    override fun isVisibleBy(viewer: Participant): Boolean = true
}