package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.all
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class JinrouGameTest {
    private val testDefaultDispatcher = StandardTestDispatcher()
    private val testScope = CoroutineScope(testDefaultDispatcher)

    @Test
    fun observeParticipantsTest() = runTest {
        val participantA1 = Participant(UUID.randomUUID(), mockk(), mockk())
        val participantA2 = participantA1.copy(state = ParticipantStates.DEAD)
        val participantB = Participant(UUID.randomUUID(), mockk(), mockk())
        val participantC = Participant(UUID.randomUUID(), mockk(), mockk())

        val jinrouGame = JinrouGame(listOf(participantA1, participantB).all())
        val emitted = mutableListOf<Participant>()

        jinrouGame.observeParticipants(testScope).onEach {
            emitted.add(it)
        }.launchIn(testScope)

        jinrouGame.updateParticipants(listOf(participantA1, participantB))

        testDefaultDispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf<Participant>(), emitted)

        emitted.clear()

        jinrouGame.updateParticipants(listOf(participantA2, participantB, participantC))

        testDefaultDispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(participantA2, participantC), emitted)
    }
}