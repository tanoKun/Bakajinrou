package com.github.tanokun.bakajinrou.plugin.position.wolf

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition

object MadmanSecondPosition: MadmanPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "狂人", defaultPrefix = "狂人")

    override val publicPosition: Position = CitizenPosition

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {}
}