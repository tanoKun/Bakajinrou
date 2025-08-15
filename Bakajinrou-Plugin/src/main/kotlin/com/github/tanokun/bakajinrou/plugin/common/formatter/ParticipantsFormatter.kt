package com.github.tanokun.bakajinrou.plugin.common.formatter

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
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.api.translation.TranslationKey
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.FormatKeys
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
    private val translator: JinrouTranslator
) {
    private val nameCache = PlayerNameCache

    fun formatWolf(
        locale: Locale,
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(locale, it, participantKey = FormatKeys.Participant.WOLF) }
    ): Component = formatPosition<WolfPosition>(
        locale,
        categoryKey = FormatKeys.Category.WOLF,
        playerNameFormatter
    )

    fun formatMadman(
        locale: Locale,
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(locale, it, participantKey = FormatKeys.Participant.MADMAN) }
    ): Component = formatPosition<MadmanPosition>(
        locale,
        categoryKey = FormatKeys.Category.MADMAN,
        playerNameFormatter
    )

    fun formatFortune(
        locale: Locale, playerNameFormatter: (Participant) -> Component = {
            defaultPlayerNameWithIdiotFormatter<IdiotAsFortunePosition>(
                locale,
                it,
                realKeys = FormatKeys.Participant.Mystic.FORTUNE,
                idiotKeys = FormatKeys.Participant.Idiot.FORTUNE
            )
        }
    ): Component = formatPositionWithIdiot<FortunePosition, IdiotAsFortunePosition>(
        locale,
        categoryKey = FormatKeys.Category.FORTUNE,
        playerNameFormatter
    )

    fun formatMedium(
        locale: Locale, playerNameFormatter: (Participant) -> Component = {
            defaultPlayerNameWithIdiotFormatter<IdiotAsMediumPosition>(
                locale,
                it,
                realKeys = FormatKeys.Participant.Mystic.MEDIUM,
                idiotKeys = FormatKeys.Participant.Idiot.MEDIUM
            )
        }
    ): Component = formatPositionWithIdiot<MediumPosition, IdiotAsMediumPosition>(
        locale,
        categoryKey = FormatKeys.Category.MEDIUM,
        playerNameFormatter
    )

    fun formatKnight(
        locale: Locale, playerNameFormatter: (Participant) -> Component = {
            defaultPlayerNameWithIdiotFormatter<IdiotAsKnightPosition>(
                locale,
                it,
                realKeys = FormatKeys.Participant.Mystic.KNIGHT,
                idiotKeys = FormatKeys.Participant.Idiot.KNIGHT
            )
        }
    ): Component = formatPositionWithIdiot<KnightPosition, IdiotAsKnightPosition>(
        locale,
        categoryKey = FormatKeys.Category.KNIGHT,
        playerNameFormatter
    )

    fun formatFox(
        locale: Locale,
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(locale, it, participantKey = FormatKeys.Participant.FOX) }
    ): Component = formatPosition<FoxPosition>(
        locale,
        categoryKey = FormatKeys.Category.FOX,
        playerNameFormatter
    )

    fun formatCitizen(
        locale: Locale,
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(locale, it, participantKey = FormatKeys.Participant.CITIZEN) }
    ): Component = formatPosition<CitizenPosition>(
        locale,
        categoryKey = FormatKeys.Category.CITIZEN,
        playerNameFormatter
    )

    private inline fun <reified T: Position> formatPosition(
        locale: Locale, categoryKey: TranslationKey, noinline formatter: (Participant) -> Component
    ): Component {
        val positionLine = positionLineComponent(participants.includes { it.isPosition<T>() }, formatter)

        return component {
            raw { translator.translate(categoryKey, locale) }
            newline()
            text("  ")
            positionLine?.let { raw { it } }
        }
    }

    private inline fun <reified T: Position, reified I: IdiotPosition> formatPositionWithIdiot(
        locale: Locale, categoryKey: TranslationKey, noinline formatter: (Participant) -> Component
    ): Component {
        val idiotsAsFortune = participants.includes { it.isPosition<I>() }

        val componentAsJob = formatPosition<T>(locale, categoryKey, formatter)

        return component {
            raw { componentAsJob }

            positionLineComponent(idiotsAsFortune, formatter)?.let { line ->
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

    private fun defaultPlayerNameComponent(locale: Locale, target: Participant, participantKey: TranslationKey): Component {
        val playerName = component { text(nameCache.get(target) ?: "unknown") }

        return translator.translate(participantKey, locale, playerName)
    }

    private inline fun <reified I: IdiotPosition> defaultPlayerNameWithIdiotFormatter(
        locale: Locale,
        target: Participant,
        realKeys: TranslationKey,
        idiotKeys: TranslationKey,
    ): Component {
        val playerName = component { text(nameCache.get(target) ?: "unknown") }

        if (target.position is I) {
            return translator.translate(idiotKeys, locale, playerName)
        }

        return defaultPlayerNameComponent(locale, target, realKeys)
    }
}