package com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.method.optional.position.FortuneBookItem
import com.github.tanokun.bakajinrou.plugin.participant.ability.fortune.FakeFortuneUsableAbility
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions

object IdiotAsFortunePosition: IdiotAsJobPosition("占い師", Positions.Fortune.color) {
    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val fortuneBook = FortuneBookItem(FakeFortuneUsableAbility(), participants.nonSpectators())

        self.grantMethod(fortuneBook)
    }
}