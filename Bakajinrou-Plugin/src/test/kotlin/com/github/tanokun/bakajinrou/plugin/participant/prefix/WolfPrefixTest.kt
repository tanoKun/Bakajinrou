package com.github.tanokun.bakajinrou.plugin.participant.prefix

import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.CitizenPosition
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContains

class WolfPrefixTest {
    private val wolfPrefix: WolfPrefix

    init {
        val madman = mockk<Participant> {
            every { position } returns mockk<MadmanPosition>()
        }
        val known = ParticipantScope.NonSpectators(listOf(madman))
        wolfPrefix = WolfPrefix(known)
    }

    @Test
    @DisplayName("観察者が死亡状態の場合、人狼Prefixを返す")
    fun returnWolfPrefixIfViewerIsDead() {
        val viewer = mockk<Participant> {
            every { position } returns mockk<CitizenPosition>()
            every { state } returns ParticipantStates.DEAD
        }
        val target = mockk<Participant>()

        val result = wolfPrefix.resolvePrefix(viewer, target)
        assertContains(result.toString(), "人狼")
    }

    @Test
    @DisplayName("観察者が観戦者の場合、人狼Prefixを返す")
    fun returnWolfPrefixIfViewerIsSpectator() {
        val viewer = mockk<Participant> {
            every { position } returns mockk<SpectatorPosition>()
            every { state } returns ParticipantStates.SURVIVED
        }
        val target = mockk<Participant>()

        val result = wolfPrefix.resolvePrefix(viewer, target)
        assertContains(result.toString(), "人狼")
    }

    @Test
    @DisplayName("観察者が knownByMadmans に含まれる場合、人狼Prefixを返す")
    fun returnWolfPrefixIfViewerIsKnownByMadman() {
        val viewer = mockk<Participant> {
            every { position } returns mockk<MadmanPosition>()
            every { state } returns ParticipantStates.SURVIVED
        }
        val known = ParticipantScope.NonSpectators(listOf(viewer))
        val prefix = WolfPrefix(known)

        val target = mockk<Participant>()

        val result = prefix.resolvePrefix(viewer, target)
        assertContains(result.toString(), "人狼")
    }

    @Test
    @DisplayName("観察者と対象が同じ場合、人狼Prefixを返す")
    fun returnWolfPrefixIfViewerIsTarget() {
        val viewer = mockk<Participant>() {
            every { position } returns mockk<CitizenPosition>()
            every { state } returns ParticipantStates.SURVIVED
        }

        val known = ParticipantScope.NonSpectators(emptyList())
        val prefix = WolfPrefix(known)

        val result = prefix.resolvePrefix(viewer, viewer)
        assertContains(result.toString(), "人狼")
    }

    @Test
    @DisplayName("どの条件にも当てはまらない場合、nullを返す")
    fun returnNullIfNoMatch() {
        val viewer = mockk<Participant> {
            every { position } returns mockk<CitizenPosition>()
            every { state } returns ParticipantStates.SURVIVED
        }
        val target = mockk<Participant>()

        val known = ParticipantScope.NonSpectators(emptyList())
        val prefix = WolfPrefix(known)

        val result = prefix.resolvePrefix(viewer, target)
        assertNull(result)
    }

    @Test
    @DisplayName("狂人以外の役職が knownByMadmans に含まれている場合、例外を投げる")
    fun throwsIfNonMadmanIncluded() {
        val nonMadman = mockk<Participant> {
            every { position } returns mockk<CitizenPosition>() // 狂人以外
        }
        val known = ParticipantScope.NonSpectators(listOf(nonMadman))

        assertThrows<IllegalStateException> {
            WolfPrefix(known)
        }
    }
}