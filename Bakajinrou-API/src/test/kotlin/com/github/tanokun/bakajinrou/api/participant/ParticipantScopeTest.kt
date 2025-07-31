package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class ParticipantScopeTest {
    val participant1 = Participant(UUID.randomUUID(), mockk<WolfPosition>(), mockk(), ParticipantStates.DEAD)
    val participant2 = Participant(UUID.randomUUID(), mockk<SpectatorPosition>(), mockk())
    val participant3 = Participant(UUID.randomUUID(), mockk<CitizensPosition>(), mockk())

    val all = ParticipantScope.All(listOf(participant1, participant2, participant3))

    @Test
    @DisplayName("nonSpectator は全観戦者を除外")
    fun testNonSpectators() {
        val result = all.nonSpectators()
        Assertions.assertFalse(result.contains(participant2))
        Assertions.assertTrue(result.containsAll(listOf(participant1, participant3)))
        Assertions.assertEquals(2, result.size)
    }

    @Test
    @DisplayName("survivedOnly は生存者のみ")
    fun testSurvivedOnly() {
        val result = all.survivedOnly()

        Assertions.assertFalse(result.contains(participant1))
        Assertions.assertFalse(result.contains(participant2))
        Assertions.assertTrue(result.contains(participant3))
        Assertions.assertEquals(1, result.size)
    }

    @Test
    @DisplayName("position は指定した役職のみ")
    fun testPositionFilter() {
        val result = all.position<WolfPosition>()

        Assertions.assertTrue(result.contains(participant1))
        Assertions.assertEquals(1, result.size)
    }

    @Test
    @DisplayName("excludePosition は指定した役職を除外")
    fun testExcludePosition() {
        val result = all.excludePosition<WolfPosition>()

        Assertions.assertFalse(result.contains(participant1))
        Assertions.assertTrue(result.containsAll(listOf(participant2, participant3)))
        Assertions.assertEquals(2, result.size)
    }

    @Test
    @DisplayName("exclude は指定した参加者を除外")
    fun testExcludeParticipant() {
        val result = all.exclude(participant3)

        Assertions.assertFalse(result.contains(participant3))
        Assertions.assertTrue(result.containsAll(listOf(participant1, participant2)))
        Assertions.assertEquals(2, result.size)
    }
}