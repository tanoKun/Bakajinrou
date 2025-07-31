package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.gui.ability.medium.CorrectMediumUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.optional.position.MediumHeartItem

object MediumPosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "霊媒師", defaultPrefix = "霊媒師")

    override val publicPosition: Position = CitizenPosition

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val mediumHeart = MediumHeartItem(CorrectMediumUsableAbility, participants.nonSpectators())

        self.grantMethod(mediumHeart)
    }
}