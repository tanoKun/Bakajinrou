package com.github.tanokun.bakajinrou.api.participant.position

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertNull
import kotlin.test.Test
import kotlin.test.assertEquals

class PrefixTest {

    private val revealedPrefix = "[Revealed]"
    private val defaultPrefix = "[Default]"
    private val prefix = Prefix(revealedPrefix, defaultPrefix)

    @Test
    @DisplayName("観察者が観戦者なら完全なプレフィックスを返す")
    fun getRevealedTestBySpectator() {
        val viewer = mockk<Participant>().apply {
            every { position } returns mockk<SpectatorPosition>()
            every { state } returns ParticipantStates.SURVIVED
        }

        val target = mockk<Participant>()

        val result = prefix.resolvePrefix(viewer, target)
        assertEquals(revealedPrefix, result)
    }

    @Test
    @DisplayName("観察者が観戦者なら完全なプレフィックスを返す")
    fun getRevealedTestByDead() {
        val viewer = mockk<Participant>().apply {
            every { position } returns mockk<Position>()
            every { state } returns ParticipantStates.DEAD
        }

        val target = mockk<Participant>()

        val result = prefix.resolvePrefix(viewer, target)
        assertEquals(revealedPrefix, result)
    }

    @Test
    @DisplayName("観察者がViewerの条件を満たしている場合は生存時のプレフィックスを返す")
    fun getDefaultTestByCompleteViewer() {
        val viewer = mockk<Participant>().apply {
            every { position } returns mockk<Position>()
            every { state } returns ParticipantStates.SURVIVED
        }

        val target = mockk<Participant>().apply {
            every { position } returns mockk<Position>().apply {
                every { isVisibleBy(viewer) } returns true
            }
        }

        val result = prefix.resolvePrefix(viewer, target)
        assertEquals(defaultPrefix, result)
    }

    @Test
    @DisplayName("観察者が被観察者と同一人物なら生存時のプレフィックスを返す")
    fun getDefaultTestBySameParticipants() {
        val viewer = mockk<Participant>().apply viewer@ {
            every { position } returns mockk<Position>().apply {
                every { isVisibleBy(this@viewer) } returns false
            }
            every { state } returns ParticipantStates.SURVIVED

        }
        val target = viewer

        val result = prefix.resolvePrefix(viewer, target)
        assertEquals(defaultPrefix, result)
    }

    @Test
    @DisplayName("どの条件も満たさない場合はnullを返す")
    fun getNullTest() {
        val viewer = mockk<Participant>().apply {
            every { position } returns mockk<Position>()
            every { state } returns ParticipantStates.SURVIVED
        }

        val target = mockk<Participant>().apply {
            every { position } returns mockk<Position>().apply {
                every { isVisibleBy(viewer) } returns false
            }
        }

        val result = prefix.resolvePrefix(viewer, target)
        assertNull(result)
    }
}