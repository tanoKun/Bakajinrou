package com.github.tanokun.bakajinrou.game.audience

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.player.asPlayerId
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
class GameAudienceStoreTest {
    @Test
    fun `参加者は観戦者として登録できない`() = runTest {
        val participant = participant()
        val game = game(participant)

        shouldThrow<IllegalArgumentException> {
            GameAudienceStore(game, GameAudience(setOf(participant.playerId)))
        }

        val audience = GameAudienceStore(game)
        audience.joinSpectator(participant.playerId) shouldBe JoinSpectatorResult.ParticipantCannotSpectate
    }

    @Test
    fun `途中参加と退出を現在状態と変更Flowへ反映する`() = runTest {
        val spectatorId = UUID.randomUUID().asPlayerId()
        val audience = GameAudienceStore(game())
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
        val audience = GameAudienceStore(game())

        audience.joinSpectator(spectatorId)

        val next = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            audience.changes.first() shouldBe AudienceChange.Joined(nextSpectatorId)
        }
        audience.joinSpectator(nextSpectatorId)
        next.join()
    }

    private fun participant() = Participant(
        UUID.randomUUID().asParticipantId(),
        mockk(),
        GrantedStrategy(emptyMap()),
    )

    private fun game(vararg participants: Participant) =
        GameStore(JinrouGame(participants.asIterable().all()))
}
