package com.github.tanokun.bakajinrou.bukkit.finishing

import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import com.github.tanokun.bakajinrou.api.finishing.JinrouGameFinishDecider
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.participant.protection.Protection
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertTrue

class FinisherDecidedSideTest {
    val citizenSideFinisherMock = mockk<GameFinisher>()
    val wolfSideFinisherMock = mockk<GameFinisher>()
    val foxSideFinisherMock = mockk<GameFinisher>()

    val gameFinishDecider = JinrouGameFinishDecider(
        { citizenSideFinisherMock },
        { wolfSideFinisherMock },
        { foxSideFinisherMock },
    )


    @Test
    @DisplayName("村人勝利 (人狼、妖狐全滅)")
    fun citizensWinTest() {
        val participants = createParticipants(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = true, isDeadMadman = true, isDeadFox = true
        )
        val finisher = gameFinishDecider.decide(participants)

        assertTrue("勝利サイドは村人なはず") { finisher == citizenSideFinisherMock }
    }

    @Test
    @DisplayName("人狼勝利 (村人、妖狐全滅)")
    fun wolfsWinTest() {
        val participants = createParticipants(
            isDeadCitizen1 = true, isDeadCitizen2 = true, isDeadWolf = false, isDeadMadman = false, isDeadFox = true
        )
        val finisher = gameFinishDecider.decide(participants)

        assertTrue("勝利サイドは人狼なはず") { finisher == wolfSideFinisherMock }
    }

    @Test
    @DisplayName("妖狐勝利 (村人全滅、人狼生存)")
    fun foxWinWithWolfsTest() {
        val participants = createParticipants(
            isDeadCitizen1 = true, isDeadCitizen2 = true, isDeadWolf = false, isDeadMadman = false, isDeadFox = false
        )
        val finisher = gameFinishDecider.decide(participants)

        assertTrue("勝利サイドは妖狐なはず") { finisher == foxSideFinisherMock }
    }

    @Test
    @DisplayName("妖狐勝利 (人狼全滅、村人生存)")
    fun foxWinWithCitizensTest() {
        val participants = createParticipants(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = true, isDeadMadman = true, isDeadFox = false
        )
        val finisher = gameFinishDecider.decide(participants)

        assertTrue("勝利サイドは妖狐なはず") { finisher == foxSideFinisherMock }
    }

    @Test
    @DisplayName("勝つに適切ではない条件での比較")
    fun noOneWinTest() {
        val participants = createParticipants(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = false, isDeadMadman = false, isDeadFox = false
        )
        val finisher = gameFinishDecider.decide(participants)
        assertTrue("勝利者がいないので、Nullのはず") { finisher == null }

        val participants2 = createParticipants(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = false, isDeadMadman = true, isDeadFox = false
        )
        val finisher2 = gameFinishDecider.decide(participants2)
        assertTrue("勝利者がいないので、Nullのはず") { finisher2 == null }

        val participants3 = createParticipants(
            isDeadCitizen1 = true, isDeadCitizen2 = false, isDeadWolf = false, isDeadMadman = true, isDeadFox = true
        )
        val finisher3 = gameFinishDecider.decide(participants3)
        assertTrue("勝利者がいないので、Nullのはず") { finisher3 == null }
    }

    private fun createParticipants(
        isDeadCitizen1: Boolean, isDeadCitizen2: Boolean, isDeadFox: Boolean, isDeadWolf: Boolean, isDeadMadman: Boolean,
    ) = listOf(
        Participant(UUID.randomUUID(), mockk<CitizensPosition>(), mockk<Protection>()).apply { if (isDeadCitizen1) dead() },
        Participant(UUID.randomUUID(), mockk<CitizensPosition>(), mockk<Protection>()).apply { if (isDeadCitizen2) dead() },
        Participant(UUID.randomUUID(), mockk<FoxPosition>(), mockk<Protection>()).apply { if (isDeadFox) dead() },
        Participant(UUID.randomUUID(), mockk<WolfPosition>(), mockk<Protection>()).apply { if (isDeadWolf) dead() },
        Participant(UUID.randomUUID(), mockk<MadmanPosition>(), mockk<Protection>()).apply { if (isDeadMadman) dead() },
    )

}