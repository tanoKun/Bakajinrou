package com.github.tanokun.bakajinrou.plugin.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.gui.ability.knight.FakeKnightUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.optional.position.KnightGrantItem

object IdiotAsKnightPosition: IdiotAsJobPosition("騎士") {
    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val knightGrant = KnightGrantItem(FakeKnightUsableAbility, participants.nonSpectators())

        self.grantMethod(knightGrant)
    }
}