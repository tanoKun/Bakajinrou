package com.github.tanokun.bakajinrou.plugin.position.citizen.idiot

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.plugin.gui.ability.fortune.FakeFortuneUsableAbility
import com.github.tanokun.bakajinrou.plugin.method.optional.position.FortuneBookItem

object IdiotAsFortunePosition: IdiotAsJobPosition("占い師") {
    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {
        val fortuneBook = FortuneBookItem(FakeFortuneUsableAbility(), participants.nonSpectators())

        self.grantMethod(fortuneBook)
    }
}