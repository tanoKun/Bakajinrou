package com.github.tanokun.bakajinrou.plugin.formatter

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.protection.Protection
import com.github.tanokun.bakajinrou.plugin.cache.BukkitPlayerNameCache
import com.github.tanokun.bakajinrou.plugin.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.KnightPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.plugin.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.plugin.position.fox.FoxThirdPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.MadmanSecondPosition
import com.github.tanokun.bakajinrou.plugin.position.wolf.WolfSecondPosition
import io.mockk.mockk
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.junit.jupiter.api.Test
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
        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            nameCache = createCaches(),
            playerProvider = { null }
        )

        val expected = Component.text("《 人狼 》", NamedTextColor.DARK_RED)
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(Component.text("wolf1", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD))
            .append(Component.text(", ", NamedTextColor.GRAY))
            .append(Component.text("wolf2", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD))

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatWolf()))
    }

    @Test
    fun formatMadmanTest() {
        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            nameCache = createCaches(),
            playerProvider = { null }
        )

        val expected = Component.text("《 狂人 》", NamedTextColor.RED)
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(Component.text("madman1", NamedTextColor.RED).decorate(TextDecoration.BOLD))
            .append(Component.text(", ", NamedTextColor.GRAY))
            .append(Component.text("madman2", NamedTextColor.RED).decorate(TextDecoration.BOLD))

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatMadman()))
    }

    @Test
    fun formatFortuneTest() {
        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            nameCache = createCaches(),
            playerProvider = { null }
        )

        val expected = Component.text("《 占い師 》", TextColor.color(0x87cefa))
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(Component.text("fortune1", TextColor.color(0x87cefa)).decorate(TextDecoration.BOLD))
            .append(Component.text(", ", NamedTextColor.GRAY))
            .append(Component.text("idiotAsFortune1(バカ)", TextColor.color(0x87cefa)).decorate(TextDecoration.BOLD))

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatFortune()))
    }

    @Test
    fun formatMediumTest() {
        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            nameCache = createCaches(),
            playerProvider = { null }
        )

        val expected = Component.text("《 霊媒師 》", TextColor.color(0xff00ff))
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(Component.text("medium1", TextColor.color(0xff00ff)).decorate(TextDecoration.BOLD))
            .append(Component.text(", ", NamedTextColor.GRAY))
            .append(Component.text("idiotAsMedium1(バカ)", TextColor.color(0xff00ff)).decorate(TextDecoration.BOLD))


        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatMedium()))
    }

    @Test
    fun formatKnightTest() {
        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            nameCache = createCaches(),
            playerProvider = { null }
        )

        val expected = Component.text("《 騎士 》", TextColor.color(0x00ff7f))
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(Component.text("knight1", TextColor.color(0x00ff7f)).decorate(TextDecoration.BOLD))
            .append(Component.text(", ", NamedTextColor.GRAY))
            .append(Component.text("idiotAsKnight1(バカ)", TextColor.color(0x00ff7f)).decorate(TextDecoration.BOLD))

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatKnight()))
    }

    @Test
    fun formatFoxTest() {
        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            nameCache = createCaches(),
            playerProvider = { null }
        )

        val expected = Component.text("《 妖狐 》", NamedTextColor.DARK_PURPLE)
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(Component.text("fox1", NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatFox()))
    }

    @Test
    fun formatCitizenTest() {
        val formatter = ParticipantsFormatter(
            participants = createParticipants(),
            nameCache = createCaches(),
            playerProvider = { null }
        )

        val expected = Component.text("《 村人 》", NamedTextColor.BLUE)
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(Component.text("citizen1", NamedTextColor.BLUE).decorate(TextDecoration.BOLD))
            .append(Component.text(", ", NamedTextColor.GRAY))
            .append(Component.text("citizen2", NamedTextColor.BLUE).decorate(TextDecoration.BOLD))

        assertEquals(serializer.serialize(expected), serializer.serialize(formatter.formatCitizen()))
    }

    fun createParticipants() = listOf(
        Participant(wolf1UniqueId, WolfSecondPosition, mockk<Protection>()),
        Participant(wolf2UniqueId, WolfSecondPosition, mockk<Protection>()),
        Participant(madman1UniqueId, MadmanSecondPosition, mockk<Protection>()),
        Participant(madman2UniqueId, MadmanSecondPosition, mockk<Protection>()),

        Participant(fortune1UniqueId, FortunePosition, mockk<Protection>()),
        Participant(idiotAsFortune1UniqueId, IdiotAsFortunePosition, mockk<Protection>()),

        Participant(medium1UniqueId, MediumPosition, mockk<Protection>()),
        Participant(idiotAsMedium1UniqueId, IdiotAsMediumPosition, mockk<Protection>()),

        Participant(knight1UniqueId, KnightPosition, mockk<Protection>()),
        Participant(idiotAsKnight1UniqueId, IdiotAsKnightPosition, mockk<Protection>()),

        Participant(fox1UniqueId, FoxThirdPosition, mockk<Protection>()),

        Participant(citizen1UniqueId, CitizenPosition, mockk<Protection>()),
        Participant(citizen2UniqueId, CitizenPosition, mockk<Protection>()),
    )

    fun createCaches() = BukkitPlayerNameCache().apply {
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