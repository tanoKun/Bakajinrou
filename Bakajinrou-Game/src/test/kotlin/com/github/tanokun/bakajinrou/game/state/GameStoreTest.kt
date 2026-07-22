package com.github.tanokun.bakajinrou.game.state

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantDifference
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategy
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class GameStoreTest {
    @Test
    fun `参加者の実変更だけを通知する`() = runTest {
        val participantA = participant()
        val participantB = participant()
        val participantC = participant()
        val store = GameStore(JinrouGame(listOf(participantA, participantB).all()))
        val emitted = mutableListOf<ParticipantDifference>()

        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            store.participantChanges.take(2).collect(emitted::add)
        }

        store.updateParticipant(participantA.participantId) { it }
        store.updateParticipant(participantA.participantId) { it.dead() }
        store.addParticipant(participantC)
        collection.join()

        emitted shouldBe listOf(
            ParticipantDifference(participantA, participantA.dead()),
            ParticipantDifference(null, participantC),
        )
    }

    @Test
    fun `購読開始前の変更は通知しない`() = runTest {
        val participantA = participant()
        val participantB = participant()
        val store = GameStore(JinrouGame(listOf(participantA).all()))

        store.updateParticipant(participantA.participantId) { it.dead() }

        val emitted = mutableListOf<ParticipantDifference>()
        val collection = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            store.participantChanges.collect(emitted::add)
        }

        emitted shouldBe emptyList()

        store.addParticipant(participantB)

        emitted shouldBe listOf(ParticipantDifference(null, participantB))
        collection.cancel()
    }

    @Test
    fun `手段の追加と削除を状態差分から導出する`() = runTest {
        val methodA = grantedMethod()
        val methodB = grantedMethod()
        val participant = participant(GrantedStrategy(mapOf(methodA.methodId to methodA)))
        val store = GameStore(JinrouGame(listOf(participant).all()))
        val changes = GameChanges(store)
        val emitted = mutableListOf<MethodDifference>()

        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            changes.methodChanges.take(2).collect(emitted::add)
        }

        store.updateParticipant(participant.participantId) {
            it.removeMethod(methodA).grantMethod(methodB)
        }
        collection.join()

        emitted shouldContainExactlyInAnyOrder listOf(
            MethodDifference.Removed(participant.participantId, methodA),
            MethodDifference.Granted(participant.participantId, methodB),
        )
    }

    private fun participant(strategy: GrantedStrategy = GrantedStrategy(emptyMap())) = Participant(
        UUID.randomUUID().asParticipantId(),
        mockk(),
        strategy,
        ParticipantStates.ALIVE,
    )

    private fun grantedMethod() = mockk<GrantedMethod> {
        every { methodId } returns mockk()
    }
}
