package com.github.tanokun.bakajinrou.game.viewer

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.player.asPlayerId
import com.github.tanokun.bakajinrou.game.audience.GameAudience
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.UUID

class GameViewersTest {
    @Test
    fun `参加状態に応じた閲覧者を検索できる`() = runTest {
        val participant = participant()
        val spectatorId = UUID.randomUUID().asPlayerId()
        val game = JinrouGame(listOf(participant).all())
        val viewers = GameViewers(
            game,
            GameAudience(setOf(spectatorId)),
        )

        viewers.find(participant.playerId) shouldBe GameViewer.Playing(participant)
        viewers.find(spectatorId) shouldBe GameViewer.Spectator(spectatorId)

        val updated = GameViewers(
            game.updateParticipant(participant.participantId) { it.dead() },
            GameAudience(setOf(spectatorId)),
        )
        updated.find(participant.playerId) shouldBe GameViewer.DeadParticipant(participant.dead())
    }

    @Test
    fun `観戦中には死亡参加者と外部観戦者だけを含む`() = runTest {
        val playing = participant()
        val dead = participant().dead()
        val spectatorId = UUID.randomUUID().asPlayerId()
        val game = JinrouGame(listOf(playing, dead).all())
        val viewers = GameViewers(
            game,
            GameAudience(setOf(spectatorId)),
        )

        viewers.all shouldBe setOf(
            GameViewer.Playing(playing),
            GameViewer.DeadParticipant(dead),
            GameViewer.Spectator(spectatorId),
        )
        viewers.spectating shouldBe setOf(
            GameViewer.DeadParticipant(dead),
            GameViewer.Spectator(spectatorId),
        )
    }

    private fun participant() = Participant(
        UUID.randomUUID().asParticipantId(),
        mockk(),
        GrantedStrategy(emptyMap()),
    )
}
