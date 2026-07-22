package com.github.tanokun.bakajinrou.game.session

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.game.state.GameStore
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.logging.Logger

@OptIn(ExperimentalCoroutinesApi::class)
class JinrouGameSessionTest {
    @Test
    fun `ドメイン状態から導出した勝利をセッションの終了結果にする`() = runTest {
        val citizen = Participant(
            UUID.randomUUID().asParticipantId(),
            mockk<CitizensPosition>(),
            GrantedStrategy(emptyMap()),
        )
        val wolf = Participant(
            UUID.randomUUID().asParticipantId(),
            mockk<WolfPosition>(),
            GrantedStrategy(emptyMap()),
        )
        val store = GameStore(JinrouGame(listOf(citizen, wolf).all()))
        val session = session(store)
        val result = async { session.observeWin().first() }

        store.updateParticipant(wolf.participantId) { it.dead() }
        runCurrent()

        result.await().shouldBeInstanceOf<WonInfo.Citizens>()
        session.lifecycle.value.shouldBeInstanceOf<GameLifecycle.Finished>()
        session.isFinished() shouldBe true
    }

    @Test
    fun `強制終了も同じ終了Flowへ流す`() = runTest {
        val citizen = Participant(
            UUID.randomUUID().asParticipantId(),
            mockk<CitizensPosition>(),
            GrantedStrategy(emptyMap()),
        )
        val wolf = Participant(
            UUID.randomUUID().asParticipantId(),
            mockk<WolfPosition>(),
            GrantedStrategy(emptyMap()),
        )
        val store = GameStore(JinrouGame(listOf(citizen, wolf).all()))
        val session = session(store)
        val result = async { session.observeWin().first() }

        session.notifyWonBySystem()
        runCurrent()

        result.await().shouldBeInstanceOf<WonInfo.System>()
        session.lifecycle.value.shouldBeInstanceOf<GameLifecycle.Finished>()
        session.isFinished() shouldBe true
    }

    private fun kotlinx.coroutines.test.TestScope.session(store: GameStore): JinrouGameSession {
        val scheduler = mockk<GameScheduler>(relaxed = true) {
            every { isActive() } returns false
        }
        return JinrouGameSession(
            game = store,
            changes = GameChanges(store),
            scheduler = scheduler,
            debug = mockk<Logger>(relaxed = true),
            topScope = backgroundScope,
        )
    }
}
