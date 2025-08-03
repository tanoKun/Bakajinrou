package com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.method.optional.position.KnightGrantItem
import com.github.tanokun.bakajinrou.plugin.participant.ability.knight.FakeKnightUsableAbility
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions

object IdiotAsKnightPosition: IdiotAsJobPosition("騎士", Positions.Knight.color) {
    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val knightGrant = KnightGrantItem(FakeKnightUsableAbility, participants.nonSpectators())

        self.grantMethod(knightGrant)
    }
}