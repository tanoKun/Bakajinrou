package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertTrue

class JinrouGameDecidedSideTest {
    @Test
    @DisplayName("市民勝利 (人狼、妖狐全滅)")
    fun citizensWinTest() {
        val game = createJinrouGame(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = true, isDeadMadman = true, isDeadFox = true
        )
        val judge = game.judge()

        assertTrue("勝利サイドは市民なはず") { judge is WonInfo.Citizens }
    }

    @Test
    @DisplayName("人狼勝利 (市民、妖狐全滅)")
    fun wolfsWinTest() {
        val game = createJinrouGame(
            isDeadCitizen1 = true, isDeadCitizen2 = true, isDeadWolf = false, isDeadMadman = false, isDeadFox = true
        )
        val judge = game.judge()

        assertTrue("勝利サイドは人狼なはず") { judge is WonInfo.Wolfs }
    }

    @Test
    @DisplayName("妖狐勝利 (市民全滅、人狼生存)")
    fun foxWinWithWolfsTest() {
        val game = createJinrouGame(
            isDeadCitizen1 = true, isDeadCitizen2 = true, isDeadWolf = false, isDeadMadman = false, isDeadFox = false
        )
        val judge = game.judge()

        assertTrue("勝利サイドは妖狐なはず") { judge is WonInfo.Fox }
    }

    @Test
    @DisplayName("妖狐勝利 (人狼全滅、市民生存)")
    fun foxWinWithCitizensTest() {
        val game = createJinrouGame(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = true, isDeadMadman = true, isDeadFox = false
        )
        val judge = game.judge()

        assertTrue("勝利サイドは妖狐なはず") { judge is WonInfo.Fox }
    }

    @Test
    @DisplayName("勝つに適切ではない条件での比較")
    fun noOneWinTest() {
        val game = createJinrouGame(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = false, isDeadMadman = false, isDeadFox = false
        )
        val judge = game.judge()
        assertTrue("勝利者がいないので、Nullのはず") { judge == null }

        val game2 = createJinrouGame(
            isDeadCitizen1 = false, isDeadCitizen2 = false, isDeadWolf = false, isDeadMadman = true, isDeadFox = false
        )
        val judge2 = game2.judge()
        assertTrue("勝利者がいないので、Nullのはず") { judge2 == null }

        val game3 = createJinrouGame(
            isDeadCitizen1 = true, isDeadCitizen2 = false, isDeadWolf = false, isDeadMadman = true, isDeadFox = true
        )
        val judge3 = game3.judge()
        assertTrue("勝利者がいないので、Nullのはず") { judge3 == null }
    }

    private fun createJinrouGame(
        isDeadCitizen1: Boolean, isDeadCitizen2: Boolean, isDeadFox: Boolean, isDeadWolf: Boolean, isDeadMadman: Boolean,
    ) = JinrouGame(
        UpdateMutexProvider(),
        setOf(
            Participant(UUID.randomUUID().asParticipantId(), mockk<CitizensPosition>(), mockk<GrantedStrategy>()).let { if (isDeadCitizen1) it.dead() else it },
            Participant(UUID.randomUUID().asParticipantId(), mockk<CitizensPosition>(), mockk<GrantedStrategy>()).let { if (isDeadCitizen2) it.dead() else it },
            Participant(UUID.randomUUID().asParticipantId(), mockk<FoxPosition>(), mockk<GrantedStrategy>()).let { if (isDeadFox) it.dead() else it },
            Participant(UUID.randomUUID().asParticipantId(), mockk<WolfPosition>(), mockk<GrantedStrategy>()).let { if (isDeadWolf) it.dead() else it },
            Participant(UUID.randomUUID().asParticipantId(), mockk<MadmanPosition>(), mockk<GrantedStrategy>()).let { if (isDeadMadman) it.dead() else it },
        ).all(),
    )
}