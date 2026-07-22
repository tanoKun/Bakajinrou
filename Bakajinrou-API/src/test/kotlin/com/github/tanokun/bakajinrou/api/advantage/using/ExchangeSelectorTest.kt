package com.github.tanokun.bakajinrou.api.advantage.using

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.random.Random

class ExchangeSelectorTest {
    private val selector = ExchangeSelector(Random(0))
    private val userId = ParticipantId(UUID.randomUUID())

    @Test
    fun excludesUserFromCandidates() {
        val otherId = ParticipantId(UUID.randomUUID())

        assertEquals(otherId, selector.select(userId, setOf(userId, otherId)))
    }

    @Test
    fun selectsOnlyFromGivenCandidateIds() {
        val candidates = setOf(
            ParticipantId(UUID.randomUUID()),
            ParticipantId(UUID.randomUUID()),
        )

        val selected = selector.select(userId, candidates + userId)

        assertNotEquals(userId, selected)
        assert(selected in candidates)
    }
}
