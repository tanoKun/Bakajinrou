package com.github.tanokun.bakajinrou.plugin.formatter

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.nonSpectators
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.participant.ParticipantStrategy
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.KnightPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsMediumPosition
import io.mockk.mockk
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.junit.jupiter.api.Test
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.newline
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.text
import java.util.*
import kotlin.test.assertEquals

class ParticipantsFormatterTest {
    private val wolf1UniqueId = UUID.randomUUID()
    private val wolf2UniqueId = UUID.randomUUID()

    private val madman1UniqueId = UUID.randomUUID()
    private val madman2UniqueId = UUID.randomUUID()

    private val fortune1UniqueId = UUID.randomUUID()
    private val idiotAsFortune1UniqueId = UUID.randomUUID()

    private val medium1UniqueId = UUID.randomUUID()
    private val idiotAsMedium1UniqueId = UUID.randomUUID()

    private val knight1UniqueId = UUID.randomUUID()
    private val idiotAsKnight1UniqueId = UUID.randomUUID()

    private val fox1UniqueId = UUID.randomUUID()

    private val citizen1UniqueId = UUID.randomUUID()
    private val citizen2UniqueId = UUID.randomUUID()

    private val serializer = LegacyComponentSerializer.legacySection()

    @Test
    fun formatWolfTest() {
        createCaches()

        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            playerProvider = { null }
        )

        val wolfColor = NamedTextColor.DARK_RED.asHexString()

        val expected = component {
            text("《 ") color gray
            text("人狼") color wolfColor deco bold
            text(" 》") color gray
            newline()
            text("  ")
            text("wolf1") color wolfColor deco bold
            text(", ") color gray
            text("wolf2") color wolfColor deco bold
        }

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatWolf()))
    }

    @Test
    fun formatMadmanTest() {
        createCaches()

        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            playerProvider = { null }
        )
        
        val madmanColor = NamedTextColor.RED.asHexString()

        val expected = component {
            text("《 ") color gray
            text("狂人") color madmanColor deco bold
            text(" 》") color gray
            newline()
            text("  ")
            text("madman1") color madmanColor deco bold
            text(", ") color gray
            text("madman2") color madmanColor deco bold
        }

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatMadman()))
    }

    @Test
    fun formatFortuneTest() {
        createCaches()

        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            playerProvider = { null }
        )

        val fortuneColor = TextColor.color(0x87cefa).asHexString()

        val expected = component {
            text("《 ") color gray
            text("占い師") color fortuneColor deco bold
            text(" 》") color gray
            newline()
            text("  ")
            text("fortune1") color fortuneColor deco bold
            text(", ") color gray
            text("idiotAsFortune1") color fortuneColor deco bold
            text("(バカ)") color fortuneColor deco bold
        }

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatFortune()))
    }

    @Test
    fun formatMediumTest() {
        createCaches()

        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            playerProvider = { null }
        )


        val mediumColor = TextColor.color(0xff00ff).asHexString()

        val expected = component {
            text("《 ") color gray
            text("霊媒師") color mediumColor deco bold
            text(" 》") color gray
            newline()
            text("  ")
            text("medium1") color mediumColor deco bold
            text(", ") color gray
            text("idiotAsMedium1") color mediumColor deco bold
            text("(バカ)") color mediumColor deco bold
        }

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatMedium()))
    }

    @Test
    fun formatKnightTest() {
        createCaches()

        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            playerProvider = { null }
        )

        val knightColor = TextColor.color(0x00ff7f).asHexString()

        val expected = component {
            text("《 ") color gray
            text("騎士") color knightColor deco bold
            text(" 》") color gray
            newline()
            text("  ")
            text("knight1") color knightColor deco bold
            text(", ") color gray
            text("idiotAsKnight1") color knightColor deco bold
            text("(バカ)") color knightColor deco bold
        }

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatKnight()))
    }

    @Test
    fun formatFoxTest() {
        createCaches()

        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            playerProvider = { null }
        )

        val foxColor = NamedTextColor.DARK_PURPLE.asHexString()

        val expected = component {
            text("《 ") color gray
            text("妖狐") color foxColor deco bold
            text(" 》") color gray
            newline()
            text("  ")
            text("fox1") color foxColor deco bold
        }

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatFox()))
    }

    @Test
    fun formatCitizenTest() {
        createCaches()

        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            playerProvider = { null }
        )

        val citizenColor = NamedTextColor.BLUE.asHexString()

        val expected = component {
            text("《 ") color gray
            text("市民") color citizenColor deco bold
            text(" 》") color gray
            newline()
            text("  ")
            text("citizen1") color citizenColor deco bold
            text(", ") color gray
            text("citizen2") color citizenColor deco bold
        }

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatCitizen()))
    }

    fun createParticipants() = listOf(
        Participant(wolf1UniqueId, mockk<WolfPosition>(), mockk<ParticipantStrategy>()),
        Participant(wolf2UniqueId, mockk<WolfPosition>(), mockk<ParticipantStrategy>()),
        Participant(madman1UniqueId, mockk<MadmanPosition>(), mockk<ParticipantStrategy>()),
        Participant(madman2UniqueId, mockk<MadmanPosition>(), mockk<ParticipantStrategy>()),

        Participant(fortune1UniqueId, mockk<FortunePosition>(), mockk<ParticipantStrategy>()),
        Participant(idiotAsFortune1UniqueId, mockk<IdiotAsFortunePosition>(), mockk<ParticipantStrategy>()),

        Participant(medium1UniqueId, mockk<MediumPosition>(), mockk<ParticipantStrategy>()),
        Participant(idiotAsMedium1UniqueId, mockk<IdiotAsMediumPosition>(), mockk<ParticipantStrategy>()),

        Participant(knight1UniqueId, mockk<KnightPosition>(), mockk<ParticipantStrategy>()),
        Participant(idiotAsKnight1UniqueId, mockk<IdiotAsKnightPosition>(), mockk<ParticipantStrategy>()),

        Participant(fox1UniqueId, mockk<FoxPosition>(), mockk<ParticipantStrategy>()),

        Participant(citizen1UniqueId, mockk<CitizenPosition>(), mockk<ParticipantStrategy>()),
        Participant(citizen2UniqueId, mockk<CitizenPosition>(), mockk<ParticipantStrategy>()),
    ).nonSpectators()

    fun createCaches() = PlayerNameCache.apply {
        put(wolf1UniqueId, "wolf1")
        put(wolf2UniqueId, "wolf2")
        put(madman1UniqueId, "madman1")
        put(madman2UniqueId, "madman2")

        put(fortune1UniqueId, "fortune1")
        put(idiotAsFortune1UniqueId, "idiotAsFortune1")

        put(medium1UniqueId, "medium1")
        put(idiotAsMedium1UniqueId, "idiotAsMedium1")

        put(knight1UniqueId, "knight1")
        put(idiotAsKnight1UniqueId, "idiotAsKnight1")

        put(fox1UniqueId, "fox1")

        put(citizen1UniqueId, "citizen1")
        put(citizen2UniqueId, "citizen2")
    }
}