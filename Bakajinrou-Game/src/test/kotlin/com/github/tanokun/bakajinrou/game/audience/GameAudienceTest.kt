package com.github.tanokun.bakajinrou.game.audience

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.player.asPlayerId
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.game.state.GameStore
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class GameAudienceTest {
    @Test
    fun `参加者は観戦者として登録できない`() = runTest {
        val participant = participant()
        val game = game(participant)

        shouldThrow<IllegalArgumentException> {
            GameAudience(game, setOf(participant.playerId))
        }

        val audience = GameAudience(game)
        audience.joinSpectator(participant.playerId) shouldBe JoinSpectatorResult.ParticipantCannotSpectate
    }

    @Test
    fun `途中参加と退出を現在状態と変更Flowへ反映する`() = runTest {
        val game = game()
        val spectatorId = UUID.randomUUID().asPlayerId()
        val audience = GameAudience(game)
        val emitted = mutableListOf<AudienceChange>()
        val collection = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            audience.changes.collect(emitted::add)
        }

        audience.joinSpectator(spectatorId) shouldBe JoinSpectatorResult.Joined
        audience.current.spectators shouldBe setOf(spectatorId)
        audience.leaveSpectator(spectatorId) shouldBe true
        audience.current.spectators shouldBe emptySet()
        emitted shouldBe listOf(AudienceChange.Joined(spectatorId), AudienceChange.Left(spectatorId))

        collection.cancel()
    }

    @Test
    fun `変更Flowは過去の観戦参加を再生しない`() = runTest {
        val spectatorId = UUID.randomUUID().asPlayerId()
        val nextSpectatorId = UUID.randomUUID().asPlayerId()
        val audience = GameAudience(game())

        audience.joinSpectator(spectatorId)

        val next = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            audience.changes.first() shouldBe AudienceChange.Joined(nextSpectatorId)
        }
        audience.joinSpectator(nextSpectatorId)
        next.join()
    }

    @Test
    fun `死亡した参加者と外部観戦者を同じ観戦表示へ投影する`() = runTest {
        val participant = participant()
        val spectatorId = UUID.randomUUID().asPlayerId()
        val game = game(participant)
        val audience = GameAudience(game, setOf(spectatorId))
        val viewers = GameViewers(game, audience, GameChanges(game))

        viewers.resolve(participant.playerId) shouldBe GameViewer.Playing(participant)
        viewers.resolve(spectatorId) shouldBe GameViewer.Spectator(spectatorId)

        game.updateParticipant(participant.participantId) { it.dead() }

        viewers.resolve(participant.playerId) shouldBe GameViewer.DeadParticipant(participant.dead())
        viewers.allPlayerIds() shouldBe setOf(participant.playerId, spectatorId)
    }

    private fun participant() = Participant(
        UUID.randomUUID().asParticipantId(),
        mockk(),
        GrantedStrategy(emptyMap()),
    )

    private fun game(vararg participants: Participant) =
        GameStore(JinrouGame(participants.asIterable().all()))
}
