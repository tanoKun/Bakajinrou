package com.github.tanokun.bakajinrou.plugin.common.formatter

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantFilter
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
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
 *   player, player2
 * ```
 * を目的の形とするフォーマッター
 */
class ParticipantsFormatter(
    private val participants: ParticipantScope.NonSpectators,
    private val translator: JinrouTranslator
) {

    fun format(locale: Locale, categoryKey: FormatKeys.Category, vararg formatter: Pair<ParticipantFilter, FormatKeys.Participant>): Component {
        val categoryLine = translator.translate(categoryKey, locale)

        val positionLine = participants
            .asSequence()
            .mapNotNull { participant -> findMatchingFormat(participant, *formatter) }
            .map { (participant, formatInfo) -> createFormattedComponent(participant, formatInfo, locale) }
            .sortedBy { it.second }
            .map { it.first }
            .reduceOrNull { acc, comp ->
                component {
                    raw { acc }
                    text(", ") color gray
                    raw { comp }
                }
            }

        return component {
            raw { categoryLine }
            newline()
            positionLine?.let {
                text("  ")
                raw { it }
            }
        }
    }

    private fun findMatchingFormat(participant: Participant, vararg formatter: Pair<ParticipantFilter, FormatKeys.Participant>): Pair<Participant, FormatInfo>? {
        formatter.forEachIndexed { index, (filter, formatKey) ->
            if (filter(participant)) {
                return participant to FormatInfo(index, formatKey)
            }
        }

        return null
    }

    private fun createFormattedComponent(participant: Participant, formatInfo: FormatInfo, locale: Locale): Pair<Component, Int> {
        val nameComponent = Component.text(PlayerNameCache.get(participant) ?: "unknown")
        val translatedComponent = translator.translate(formatInfo.key, locale, nameComponent)

        return translatedComponent to formatInfo.order
    }

    private data class FormatInfo(val order: Int, val key: FormatKeys.Participant)
}

