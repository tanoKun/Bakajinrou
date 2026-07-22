package com.github.tanokun.bakajinrou.plugin.presentation.sidebar.game

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.FortunePosition
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.translation.PrefixKeys
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class InGameSidebarRendererTest {
    @Test
    fun `バカは偽役職ではなくバカとして集計する`() {
        listOf(
            IdiotAsFortunePosition,
            IdiotAsMediumPosition,
            IdiotAsKnightPosition,
        ).forEach { position ->
            assertEquals(PrefixKeys.IDIOT, participant(position).getDistributionPrefix())
        }
    }

    @Test
    fun `バカ以外は自身に見える役職として集計する`() {
        assertEquals(PrefixKeys.Mystic.FORTUNE, participant(FortunePosition).getDistributionPrefix())
    }

    private fun participant(position: Position) = Participant(
        participantId = UUID.randomUUID().asParticipantId(),
        position = position,
        strategy = GrantedStrategy(emptyMap()),
    )
}
