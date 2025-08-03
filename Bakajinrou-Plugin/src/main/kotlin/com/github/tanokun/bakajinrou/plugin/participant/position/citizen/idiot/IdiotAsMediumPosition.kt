package com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.method.optional.position.MediumHeartItem
import com.github.tanokun.bakajinrou.plugin.participant.ability.medium.FakeMediumUsableAbility
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions

object IdiotAsMediumPosition: IdiotAsJobPosition("霊媒師", Positions.Medium.color) {
    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val mediumHeart = MediumHeartItem(FakeMediumUsableAbility(), participants.nonSpectators())

        self.grantMethod(mediumHeart)
    }
}