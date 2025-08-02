package com.github.tanokun.bakajinrou.plugin.participant.prefix

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.CitizenPosition
import io.mockk.every
import io.mockk.mockk
import net.kyori.adventure.text.format.NamedTextColor
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertContains

@DisplayName("DefaultPrefix のテスト")
class DefaultPrefixTest {
    private val revealedPrefixText = "死亡・観戦Prefix"
    private val defaultPrefixText = "本人Prefix"
    private val color = NamedTextColor.GRAY
    private val defaultPrefix = DefaultPrefix(revealedPrefixText, defaultPrefixText, color)

    @Test
    @DisplayName("観察者が死亡状態の場合、revealedPrefixを返す")
    fun returnRevealedPrefixIfViewerIsDead() {
        val viewer = mockk<Participant> {
            every { state } returns ParticipantStates.DEAD
            every { position } returns mockk<CitizenPosition>()
        }
        val target = mockk<Participant>()

        val result = defaultPrefix.resolvePrefix(viewer, target)
        assertContains(result.toString(), revealedPrefixText)
    }

    @Test
    @DisplayName("観察者が観戦者の場合、revealedPrefixを返す")
    fun returnRevealedPrefixIfViewerIsSpectator() {
        val viewer = mockk<Participant> {
            every { position } returns mockk<SpectatorPosition>()
            every { state } returns ParticipantStates.DEAD
        }
        val target = mockk<Participant>()

        val result = defaultPrefix.resolvePrefix(viewer, target)
        assertContains(result.toString(), revealedPrefixText)
    }

    @Test
    @DisplayName("観察者と対象が同じ場合、defaultPrefixを返す")
    fun returnDefaultPrefixIfViewerIsTarget() {
        val viewer = mockk<Participant>().apply {
            every { position } returns mockk<CitizenPosition>()
            every { state } returns ParticipantStates.SURVIVED
        }

        val result = defaultPrefix.resolvePrefix(viewer, viewer)
        assertContains(result.toString(), defaultPrefixText)
    }

    @Test
    @DisplayName("どの条件にも当てはまらない場合、nullを返す")
    fun returnNullIfNoConditionMatched() {
        val viewer = mockk<Participant> {
            every { position } returns mockk<CitizenPosition>()
            every { state } returns ParticipantStates.SURVIVED
        }
        val target = mockk<Participant>()

        val result = defaultPrefix.resolvePrefix(viewer, target)
        assertNull(result)
    }
}