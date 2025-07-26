package com.github.tanokun.bakajinrou.plugin.formatter

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.citizen.IdiotPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
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
    private val participants: List<Participant>,
    private val nameCache: BukkitPlayerNameCache,
    private val playerProvider: (Participant) -> Player?
) {
    fun formatWolf(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Wolf.color) }
    ): Component = formatPosition<WolfPosition>(
        Positions.Wolf.color,
        WolfSecondPosition.prefix.defaultPrefix,
        playerNameFormatter
    )

    fun formatMadman(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Madman.color) }
    ): Component = formatPosition<MadmanPosition>(
        Positions.Madman.color,
        MadmanSecondPosition.prefix.defaultPrefix,
        playerNameFormatter
    )

    fun formatFortune(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsFortunePosition>(it, Positions.Fortune.color) }
    ): Component = formatPositionWithIdiot<FortunePosition, IdiotAsFortunePosition>(
        Positions.Fortune.color,
        FortunePosition.prefix.defaultPrefix,
        playerNameFormatter
    )

    fun formatMedium(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsMediumPosition>(it, Positions.Medium.color) }
    ): Component = formatPositionWithIdiot<MediumPosition, IdiotAsMediumPosition>(
        Positions.Medium.color,
        MediumPosition.prefix.defaultPrefix,
        playerNameFormatter
    )

    fun formatKnight(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsKnightPosition>(it, Positions.Knight.color) }
    ): Component = formatPositionWithIdiot<KnightPosition, IdiotAsKnightPosition>(
        Positions.Knight.color,
        KnightPosition.prefix.defaultPrefix,
        playerNameFormatter
    )

    fun formatFox(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Fox.color) }
    ): Component = formatPosition<FoxPosition>(
        Positions.Fox.color,
        FoxThirdPosition.prefix.defaultPrefix,
        playerNameFormatter
    )

    fun formatCitizen(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, Positions.Citizen.color) }
    ): Component = formatPosition<CitizenPosition>(
        Positions.Citizen.color,
        CitizenPosition.prefix.defaultPrefix,
        playerNameFormatter
    )
    private inline fun <reified T: Position> formatPosition(positionColor: TextColor, description: String, noinline formatter: (Participant) -> Component): Component {
        val positionLine = positionLineComponent(participants.filter { it.position is T }, formatter)

        return component {
            text("《 $description 》") color positionColor.asHexString() deco bold
            newline()
            text("  ")
            positionLine?.let { raw { it } }
        }
    }

    private inline fun <reified T: Position, reified I: IdiotPosition> formatPositionWithIdiot(positionColor: TextColor, description: String, noinline formatter: (Participant) -> Component): Component {
        val idiotsAsFortune = participants.filter { it.position is I }

        val componentAsJob = formatPosition<T>(positionColor, description, formatter)

        return component {
            raw { componentAsJob }
            positionLineComponent(idiotsAsFortune, formatter)?.let { line ->
                text(", ") color gray
                raw { line }
            }
        }
    }

    private fun positionLineComponent(participants: List<Participant>, formatter: (Participant) -> Component): Component? =
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