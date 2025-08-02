package com.github.tanokun.bakajinrou.plugin.formatter

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.citizen.IdiotPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.KnightPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.plugin.participant.position.citizen.idiot.IdiotAsMediumPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.*
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray

/**
 * ```
 *《 役職名 》
 *   player, player2(バカ)
 * ```
 * を目的の形とするフォーマッター
 */
class ParticipantsFormatter(
    private val participants: ParticipantScope.NonSpectators,
    private val playerProvider: (Participant) -> Player?
) {
    private val nameCache = PlayerNameCache

    fun formatWolf(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Wolf.color) }
    ): Component = formatPosition<WolfPosition>(
        Positions.Wolf,
        playerNameFormatter
    )

    fun formatMadman(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Madman.color) }
    ): Component = formatPosition<MadmanPosition>(
        Positions.Madman,
        playerNameFormatter
    )

    fun formatFortune(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsFortunePosition>(it, Positions.Fortune.color) }
    ): Component = formatPositionWithIdiot<FortunePosition, IdiotAsFortunePosition>(
        Positions.Fortune,
        playerNameFormatter
    )

    fun formatMedium(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsMediumPosition>(it, Positions.Medium.color) }
    ): Component = formatPositionWithIdiot<MediumPosition, IdiotAsMediumPosition>(
        Positions.Medium,
        playerNameFormatter
    )

    fun formatKnight(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsKnightPosition>(it, Positions.Knight.color) }
    ): Component = formatPositionWithIdiot<KnightPosition, IdiotAsKnightPosition>(
        Positions.Knight,
        playerNameFormatter
    )

    fun formatFox(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Fox.color) }
    ): Component = formatPosition<FoxPosition>(
        Positions.Fox,
        playerNameFormatter
    )

    fun formatCitizen(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Citizen.color) }
    ): Component = formatPosition<CitizenPosition>(
        Positions.Citizen,
        playerNameFormatter
    )
    private inline fun <reified T: Position> formatPosition(position: Positions, noinline formatter: (Participant) -> Component): Component {
        val positionLine = positionLineComponent(participants.position<T>(), formatter)

        return component {
            text("《 ") color gray
            raw { position.createDisplayComponent() } deco bold
            text(" 》") color gray
            newline()
            text("  ")
            positionLine?.let { raw { it } }
        }
    }

    private inline fun <reified T: Position, reified I: IdiotPosition> formatPositionWithIdiot(position: Positions, noinline formatter: (Participant) -> Component): Component {
        val idiotsAsFortune = participants.position<I>()

        val componentAsJob = formatPosition<T>(position, formatter)

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

    private fun defaultPlayerNameComponent(participant: Participant, textColor: TextColor): Component {
        val playerName = playerProvider(participant)?.name ?: nameCache.get(participant.uniqueId) ?: "unknownPlayer"

        return component {
            text(playerName) color textColor.asHexString() deco bold
        }
    }

    private inline fun <reified I: IdiotPosition> defaultPlayerNameWithIdiotFormatter(participant: Participant, positionColor: TextColor): Component {
        if (participant.position is I) {
            return component {
                raw { defaultPlayerNameComponent(participant, positionColor) }
                text("(バカ)") color positionColor.asHexString() deco bold
            }
        }

        return defaultPlayerNameComponent(participant, positionColor)
    }
}