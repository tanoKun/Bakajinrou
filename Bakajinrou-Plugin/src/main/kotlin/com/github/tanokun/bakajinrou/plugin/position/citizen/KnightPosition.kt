package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.gui.ability.knight.RealKnightUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.optional.position.KnightGrantItem

object KnightPosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "騎士", defaultPrefix = "騎士")

    override val publicPosition: Position = CitizenPosition

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val knightGrant = KnightGrantItem(RealKnightUsableAbility, participants.nonSpectators())

        self.grantMethod(knightGrant)
    }
}