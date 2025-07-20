package com.github.tanokun.bakajinrou.bukkit.finishing

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.bukkit.finishing.finisher.CitizenSideFinisher
import com.github.tanokun.bakajinrou.bukkit.finishing.finisher.FoxSideFinisher
import com.github.tanokun.bakajinrou.bukkit.finishing.finisher.WolfSideFinisher
import com.github.tanokun.bakajinrou.bukkit.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.bukkit.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.bukkit.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.bukkit.position.wolf.WolfPosition
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertTrue

class FinisherDecidedSideTest {
    val noneProvider =  { throw IllegalStateException("") }

    @Test
    @DisplayName("村人勝利 (人狼、妖狐全滅)")
    fun citizensWinTest() {
        val finishHandler = JinrouGameFinishDecider()

        val citizen = Participant(UUID.randomUUID(), CitizenPosition, noneProvider)
        val fortune = Participant(UUID.randomUUID(), FortunePosition, noneProvider)

        val participants = listOf(
            citizen,
            fortune,
            Participant(UUID.randomUUID(), FoxPosition, noneProvider).apply { dead() },
            Participant(UUID.randomUUID(), WolfPosition, noneProvider).apply { dead() },
            Participant(UUID.randomUUID(), MadmanPosition, noneProvider).apply { dead() },
        )

        val finisher = finishHandler.decide(participants)

        assertTrue("勝利サイドは村人なはず") { finisher is CitizenSideFinisher }
    }

    @Test
    @DisplayName("人狼勝利 (村人、妖狐全滅)")
    fun wolfsWinTest() {
        val finishHandler = JinrouGameFinishDecider()

        val wolf = Participant(UUID.randomUUID(), WolfPosition, noneProvider)
        val madman = Participant(UUID.randomUUID(), MadmanPosition, noneProvider)

        val participants = listOf(
            Participant(UUID.randomUUID(), CitizenPosition, noneProvider).apply { dead() },
            Participant(UUID.randomUUID(), FortunePosition, noneProvider).apply { dead() },
            Participant(UUID.randomUUID(), FoxPosition, noneProvider).apply { dead() },
            wolf,
            madman,
        )

        val finisher = finishHandler.decide(participants)

        assertTrue("勝利サイドは人狼なはず") { finisher is WolfSideFinisher }
    }

    @Test
    @DisplayName("妖狐勝利 (村人全滅、人狼生存)")
    fun foxWinWithWolfsTest() {
        val finishHandler = JinrouGameFinishDecider()

        val fox = Participant(UUID.randomUUID(), FoxPosition, noneProvider)

        val participants = listOf(
            Participant(UUID.randomUUID(), CitizenPosition, noneProvider).apply { dead() },
            Participant(UUID.randomUUID(), FortunePosition, noneProvider).apply { dead() },
            fox,
            Participant(UUID.randomUUID(), WolfPosition, noneProvider),
            Participant(UUID.randomUUID(), MadmanPosition, noneProvider),
        )

        val finisher = finishHandler.decide(participants)

        assertTrue("勝利サイドは妖狐なはず") { finisher is FoxSideFinisher }
    }

    @Test
    @DisplayName("妖狐勝利 (人狼全滅、村人生存)")
    fun foxWinWithCitizensTest() {
        val finishHandler = JinrouGameFinishDecider()

        val fox = Participant(UUID.randomUUID(), FoxPosition, noneProvider)

        val participants = listOf(
            Participant(UUID.randomUUID(), CitizenPosition, noneProvider),
            Participant(UUID.randomUUID(), FortunePosition, noneProvider),
            fox,
            Participant(UUID.randomUUID(), WolfPosition, noneProvider).apply { dead() },
            Participant(UUID.randomUUID(), MadmanPosition, noneProvider).apply { dead() },
        )

        val finisher = finishHandler.decide(participants)

        assertTrue("勝利サイドは妖狐なはず") { finisher is FoxSideFinisher }
    }

    @Test
    @DisplayName("勝つに適切ではない条件での比較")
    fun noOneWinTest() {
        val finishHandler = JinrouGameFinishDecider()

        val participants = listOf(
            Participant(UUID.randomUUID(), CitizenPosition, noneProvider),
            Participant(UUID.randomUUID(), FortunePosition, noneProvider),
            Participant(UUID.randomUUID(), FoxPosition, noneProvider),
            Participant(UUID.randomUUID(), WolfPosition, noneProvider),
            Participant(UUID.randomUUID(), MadmanPosition, noneProvider),
        )

        val finisher = finishHandler.decide(participants)

        assertTrue("勝利者がいないので、Nullのはず") { finisher == null }

        val participants2 = listOf(
            Participant(UUID.randomUUID(), CitizenPosition, noneProvider),
            Participant(UUID.randomUUID(), FortunePosition, noneProvider),
            Participant(UUID.randomUUID(), FoxPosition, noneProvider),
            Participant(UUID.randomUUID(), WolfPosition, noneProvider),
            Participant(UUID.randomUUID(), MadmanPosition, noneProvider).apply { dead() },
        )

        val finisher2 = finishHandler.decide(participants2)

        assertTrue("勝利者がいないので、Nullのはず") { finisher2 == null }

        val participants3 = listOf(
            Participant(UUID.randomUUID(), CitizenPosition, noneProvider).apply { dead() },
            Participant(UUID.randomUUID(), FortunePosition, noneProvider),
            Participant(UUID.randomUUID(), FoxPosition, noneProvider),
            Participant(UUID.randomUUID(), WolfPosition, noneProvider),
            Participant(UUID.randomUUID(), MadmanPosition, noneProvider).apply { dead() },
        )

        val finisher3 = finishHandler.decide(participants3)

        assertTrue("勝利者がいないので、Nullのはず") { finisher3 == null }
    }
}