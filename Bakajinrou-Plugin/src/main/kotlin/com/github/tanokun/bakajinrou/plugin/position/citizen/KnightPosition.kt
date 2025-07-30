package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition

object KnightPosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "騎士", defaultPrefix = "騎士")

    override val publicPosition: Position = CitizenPosition

    override fun doAtStarting(participant: Participant) {}
}