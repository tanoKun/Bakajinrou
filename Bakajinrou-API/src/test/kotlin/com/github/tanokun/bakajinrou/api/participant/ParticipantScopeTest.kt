package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizensPosition
import com.github.tanokun.bakajinrou.api.participant.position.isCitizens
import com.github.tanokun.bakajinrou.api.participant.position.isMadman
import com.github.tanokun.bakajinrou.api.participant.position.isWolf
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class ParticipantScopeTest {
    private val wolf = Participant(UUID.randomUUID().asParticipantId(), mockk<WolfPosition>(), mockk(), ParticipantStates.ALIVE)
    private val madman = Participant(UUID.randomUUID().asParticipantId(), mockk<MadmanPosition>(), mockk(), ParticipantStates.ALIVE)
    private val citizen = Participant(UUID.randomUUID().asParticipantId(), mockk<CitizensPosition>(), mockk(), ParticipantStates.ALIVE)
    private val spectator = Participant(UUID.randomUUID().asParticipantId(), mockk<SpectatorPosition>(), mockk(), ParticipantStates.ALIVE)

    private val all = ParticipantScope.All(setOf(wolf, madman, citizen, spectator))

    @Test
    @DisplayName("includes: 指定フィルタに一致する参加者のみを含む")
    fun testIncludes() {
        val result = all.includes(::isWolf or ::isMadman)

        assertTrue(result.contains(wolf))
        assertTrue(result.contains(madman))
        assertFalse(result.contains(citizen))
        assertFalse(result.contains(spectator))
        assertEquals(2, result.size)
    }

    @Test
    @DisplayName("excludes: 指定フィルタに一致する参加者を除外する")
    fun testExcludes() {
        val result = all.excludes(::isCitizens)

        assertTrue(result.contains(wolf))
        assertTrue(result.contains(madman))
        assertFalse(result.contains(citizen))
        assertTrue(result.contains(spectator))
        assertEquals(3, result.size)
    }

}