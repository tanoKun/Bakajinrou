package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantDifference
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class JinrouGameTest {
    private val testDefaultDispatcher = UnconfinedTestDispatcher()
    private val testScope = CoroutineScope(testDefaultDispatcher)

    @Test
    @DisplayName("変更者のみ通知する")
    fun observeParticipantsTest() = runTest(UnconfinedTestDispatcher()) {
        val participantA1 = Participant(UUID.randomUUID().asParticipantId(), mockk(), mockk())
        val participantA2 = participantA1.copy(state = ParticipantStates.DEAD)
        val participantB = Participant(UUID.randomUUID().asParticipantId(), mockk(), mockk())
        val participantC = Participant(UUID.randomUUID().asParticipantId(), mockk(), mockk())

        val jinrouGame = JinrouGame(UpdateMutexProvider(), listOf(participantA1, participantB).all())
        val emitted = mutableListOf<ParticipantDifference>()

        testScope.launch {
            jinrouGame.observeParticipants(this).collect {
                emitted.add(it)
            }
        }

        jinrouGame.updateParticipant(participantA1.participantId) { current -> current }
        jinrouGame.updateParticipant(participantB.participantId) { current -> current }

        emitted shouldBe listOf()

        emitted.clear()

        jinrouGame.updateParticipant(participantA1.participantId) { current -> participantA2 }
        jinrouGame.addParticipant(participantC)

        emitted shouldBe listOf(ParticipantDifference(participantA1, participantA2), ParticipantDifference(null, participantC))
    }
}