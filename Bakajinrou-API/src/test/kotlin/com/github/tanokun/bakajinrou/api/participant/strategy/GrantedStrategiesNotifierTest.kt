package com.github.tanokun.bakajinrou.api.participant.strategy

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantDifference
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class GrantedStrategiesNotifierTest : ShouldSpec({
    val testDispatcher = UnconfinedTestDispatcher()
    val testScope = CoroutineScope(testDispatcher)

    val game = mockk<JinrouGame>()
    val participantsFlow = MutableSharedFlow<ParticipantDifference>(replay = 1)

    every { game.observeParticipants(testScope) } returns participantsFlow

    val notifier = GrantedStrategiesPublisher(game, testScope)

    val methodA = mockk<GrantedMethod> {
        every { methodId } returns mockk()
    }
    val methodB = mockk<GrantedMethod> {
        every { methodId } returns mockk()
    }
    val methodC = mockk<GrantedMethod> {
        every { methodId } returns mockk()
    }

    val participantId = mockk<ParticipantId>()

    val emittedDifferences = mutableListOf<MethodDifference>()

    testScope.launch {
        notifier.observeDifference()
            .collect {
                emittedDifferences.add(it)
            }
    }

    context("差分の提供者") {
        beforeEach {
            emittedDifferences.clear()
        }

        should("参加者が初めて追加された場合、所持している全ての手段がGrantedとして通知されるべき") {
            val strategy = GrantedStrategy(mapOf(methodA.methodId to methodA, methodB.methodId to methodB))
            val newParticipant = mockk<Participant> {
                every { this@mockk.participantId } returns participantId
                every { this@mockk.strategy } returns strategy
            }

            participantsFlow.emit(ParticipantDifference(null, newParticipant))

            val expected = listOf(
                MethodDifference.Granted(participantId, methodA),
                MethodDifference.Granted(participantId, methodB)
            )

            emittedDifferences shouldContainExactlyInAnyOrder expected
        }

        should("手段が追加された場合、その手段だけがGrantedとして通知されるべき") {
            val strategyBefore = GrantedStrategy(mapOf(methodA.methodId to methodA))
            val participantBefore = mockk<Participant> {
                every { this@mockk.participantId } returns participantId
                every { this@mockk.strategy } returns strategyBefore
            }

            val strategyAfter = GrantedStrategy(mapOf(methodA.methodId to methodA, methodB.methodId to methodB))
            val participantAfter = mockk<Participant> {
                every { this@mockk.participantId } returns participantId
                every { this@mockk.strategy } returns strategyAfter
            }

            participantsFlow.emit(ParticipantDifference(participantBefore, participantAfter))

            val expected = listOf(
                MethodDifference.Granted(participantId, methodB)
            )

            emittedDifferences shouldBe expected
        }

        should("手段が削除された場合、その手段だけがRemovedとして通知されるべき") {
            val strategyBefore = GrantedStrategy(mapOf(methodA.methodId to methodA, methodB.methodId to methodB))
            val participantBefore = mockk<Participant> {
                every { this@mockk.participantId } returns participantId
                every { this@mockk.strategy } returns strategyBefore
            }

            val strategyAfter = GrantedStrategy(mapOf(methodB.methodId to methodB))
            val participantAfter = mockk<Participant> {
                every { this@mockk.participantId } returns participantId
                every { this@mockk.strategy } returns strategyAfter
            }

            participantsFlow.emit(ParticipantDifference(participantBefore, participantAfter))

            val expected = listOf(
                MethodDifference.Removed(participantId, methodA)
            )

            emittedDifferences shouldBe expected
        }

        should("手段に追加・削除の両方があった場合、両方が通知されるべき") {
            val strategyBefore = GrantedStrategy(mapOf(methodA.methodId to methodA))
            val participantBefore = mockk<Participant> {
                every { this@mockk.participantId } returns participantId
                every { this@mockk.strategy } returns strategyBefore
            }

            val strategyAfter = GrantedStrategy(mapOf(methodB.methodId to methodB, methodC.methodId to methodC))
            val participantAfter = mockk<Participant> {
                every { this@mockk.participantId } returns participantId
                every { this@mockk.strategy } returns strategyAfter
            }

            participantsFlow.emit(ParticipantDifference(participantBefore, participantAfter))

            val expected = listOf(
                MethodDifference.Removed(participantId, methodA),
                MethodDifference.Granted(participantId, methodB),
                MethodDifference.Granted(participantId, methodC)
            )

            emittedDifferences shouldContainExactlyInAnyOrder expected
        }
    }
})