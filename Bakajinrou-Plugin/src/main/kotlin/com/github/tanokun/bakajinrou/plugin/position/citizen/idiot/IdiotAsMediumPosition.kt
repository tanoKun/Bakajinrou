package com.github.tanokun.bakajinrou.plugin.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.gui.ability.medium.FakeMediumUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.optional.position.MediumHeartItem

object IdiotAsMediumPosition: IdiotAsJobPosition("霊媒師") {
    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val mediumHeart = MediumHeartItem(FakeMediumUsableAbility(), participants.nonSpectators())

        self.grantMethod(mediumHeart)
    }
}