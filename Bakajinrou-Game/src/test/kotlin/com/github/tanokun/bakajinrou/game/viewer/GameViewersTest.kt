package com.github.tanokun.bakajinrou.game.viewer

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.player.asPlayerId
import com.github.tanokun.bakajinrou.game.audience.GameAudience
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.game.state.GameStore
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
        val game = game(participant)
        val viewers = GameViewers(
            game,
            GameAudience(game, setOf(spectatorId)),
            GameChanges(game),
        )

        viewers.find(participant.playerId) shouldBe GameViewer.Playing(participant)
        viewers.find(spectatorId) shouldBe GameViewer.Spectator(spectatorId)

        game.updateParticipant(participant.participantId) { it.dead() }

        viewers.find(participant.playerId) shouldBe GameViewer.DeadParticipant(participant.dead())
    }

    @Test
    fun `観戦中には死亡参加者と外部観戦者だけを含む`() = runTest {
        val playing = participant()
        val dead = participant().dead()
        val spectatorId = UUID.randomUUID().asPlayerId()
        val game = game(playing, dead)
        val viewers = GameViewers(
            game,
            GameAudience(game, setOf(spectatorId)),
            GameChanges(game),
        )

        viewers.current shouldBe setOf(
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

    private fun game(vararg participants: Participant) =
        GameStore(JinrouGame(participants.asIterable().all()))
}
