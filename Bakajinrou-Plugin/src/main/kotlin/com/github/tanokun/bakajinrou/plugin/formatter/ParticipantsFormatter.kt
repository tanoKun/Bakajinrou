package com.github.tanokun.bakajinrou.plugin.formatter

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.idiot.IdiotPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.FortunePosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.KnightPosition
import com.github.tanokun.bakajinrou.api.participant.position.citizen.mystic.MediumPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.translate.TranslationKey
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.UITranslationKeys.Formatter
import net.kyori.adventure.text.Component
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.newline
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.text
import java.util.*


/**
 * ```
 *《 役職名 》
 *   player, player2(バカ)
 * ```
 * を目的の形とするフォーマッター
 */
class ParticipantsFormatter(
    private val participants: ParticipantScope.NonSpectators,
    private val locale: Locale,
    private val translator: JinrouTranslator
) {
    private val nameCache = PlayerNameCache

    fun formatWolf(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, participantKey = Formatter.Participant.WOLF) }
    ): Component = formatPosition<WolfPosition>(
        categoryKey = Formatter.Category.WOLF,
        playerNameFormatter
    )

    fun formatMadman(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, participantKey = Formatter.Participant.MADMAN) }
    ): Component = formatPosition<MadmanPosition>(
        categoryKey = Formatter.Category.MADMAN,
        playerNameFormatter
    )

    fun formatFortune(
        playerNameFormatter: (Participant) -> Component = {
            defaultPlayerNameWithIdiotFormatter<IdiotAsFortunePosition>(it,
                realKeys = Formatter.Participant.Mystic.FORTUNE,
                idiotKeys = Formatter.Participant.Idiot.FORTUNE
            )
        }
    ): Component = formatPositionWithIdiot<FortunePosition, IdiotAsFortunePosition>(
        categoryKey = Formatter.Category.FORTUNE,
        playerNameFormatter
    )

    fun formatMedium(
        playerNameFormatter: (Participant) -> Component = {
            defaultPlayerNameWithIdiotFormatter<IdiotAsMediumPosition>(it,
                realKeys = Formatter.Participant.Mystic.MEDIUM,
                idiotKeys = Formatter.Participant.Idiot.MEDIUM
            )
        }
    ): Component = formatPositionWithIdiot<MediumPosition, IdiotAsMediumPosition>(
        categoryKey = Formatter.Category.MEDIUM,
        playerNameFormatter
    )

    fun formatKnight(
        playerNameFormatter: (Participant) -> Component = {
            defaultPlayerNameWithIdiotFormatter<IdiotAsKnightPosition>(it,
                realKeys = Formatter.Participant.Mystic.KNIGHT,
                idiotKeys = Formatter.Participant.Idiot.KNIGHT
            )
        }
    ): Component = formatPositionWithIdiot<KnightPosition, IdiotAsKnightPosition>(
        categoryKey = Formatter.Category.KNIGHT,
        playerNameFormatter
    )

    fun formatFox(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, participantKey = Formatter.Participant.FOX) }
    ): Component = formatPosition<com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition>(
        categoryKey = Formatter.Category.FOX,
        playerNameFormatter
    )

    fun formatCitizen(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, participantKey = Formatter.Participant.CITIZEN) }
    ): Component = formatPosition<CitizenPosition>(
        categoryKey = Formatter.Category.CITIZEN,
        playerNameFormatter
    )
    private inline fun <reified T: Position> formatPosition(categoryKey: TranslationKey, noinline formatter: (Participant) -> Component): Component {
        val positionLine = positionLineComponent(participants.includes { it.isPosition<T>() }, formatter)

        return component {
            raw { translator.translate(Component.translatable(categoryKey.key), locale) }
            newline()
            text("  ")
            positionLine?.let { raw { it } }
        }
    }

    private inline fun <reified T: Position, reified I: IdiotPosition> formatPositionWithIdiot(categoryKey: TranslationKey, noinline formatter: (Participant) -> Component): Component {
        val idiotsAsFortune = participants.includes { it.isPosition<I>() }

        val componentAsJob = formatPosition<T>(categoryKey, formatter)

        return component {
            raw { componentAsJob }
            positionLineComponent(idiotsAsFortune, formatter)?.let { line ->
                text(", ") color gray
                raw { line }
            }
        }
    }

    private fun positionLineComponent(participants: ParticipantScope.NonSpectators, formatter: (Participant) -> Component): Component? =
        participants.map { formatter(it) }
            .reduceOrNull { acc, comp ->
                component {
                    raw { acc }
                    text(", ") color gray
                    raw { comp }
                }
            }

    private fun defaultPlayerNameComponent(target: Participant, participantKey: TranslationKey): Component {
        val playerName = component { text(nameCache.get(target) ?: "unknown") }

        return translator.translate(Component.translatable(participantKey.key).arguments(playerName), locale)
    }

    private inline fun <reified I: IdiotPosition> defaultPlayerNameWithIdiotFormatter(
        target: Participant,
        realKeys: TranslationKey,
        idiotKeys: TranslationKey,
    ): Component {
        val playerName = component { text(nameCache.get(target) ?: "unknown") }

        if (target.position is I) {
            return translator.translate(Component.translatable(idiotKeys.key).arguments(playerName), locale)
        }

        return defaultPlayerNameComponent(target, realKeys)
    }
}