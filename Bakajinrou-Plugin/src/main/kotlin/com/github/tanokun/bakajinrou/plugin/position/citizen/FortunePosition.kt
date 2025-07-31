package com.github.tanokun.bakajinrou.plugin.position.citizen

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.plugin.gui.ability.fortune.CorrectFortuneUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.optional.position.FortuneBookItem

object FortunePosition: CitizensPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "占い師", defaultPrefix = "占い師")

    override val publicPosition: Position = CitizenPosition

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
       val fortuneBook = FortuneBookItem(CorrectFortuneUsableAbility, participants.nonSpectators())

        self.grantMethod(fortuneBook)
    }
}