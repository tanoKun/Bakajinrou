package com.github.tanokun.bakajinrou.plugin.formatter

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.Position
import com.github.tanokun.bakajinrou.bukkit.position.citizen.CitizenPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.FortunePosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.KnightPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.MediumPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotAsFortunePosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotAsKnightPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotAsMediumPosition
import com.github.tanokun.bakajinrou.bukkit.position.citizen.idiot.IdiotPosition
import com.github.tanokun.bakajinrou.bukkit.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.bukkit.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.bukkit.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.cache.BukkitPlayerNameCache
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

/**
 * ```
 *《 役職名 》
 *   player, player2(バカ)
 * ```
 * を目的の形とするフォーマッター
 */
class ParticipantsFormatter(
    private val participants: List<Participant>,
    private val nameCache: BukkitPlayerNameCache
) {
    fun formatWolf(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, NamedTextColor.DARK_RED) }
    ): Component = formatPosition<WolfPosition>(NamedTextColor.DARK_RED, "人狼", playerNameFormatter)

    fun formatMadman(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, NamedTextColor.RED) }
    ): Component = formatPosition<MadmanPosition>(NamedTextColor.RED, "狂人", playerNameFormatter)

    fun formatFortune(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsFortunePosition>(it, TextColor.color(0x87cefa)) }
    ): Component = formatPositionWithIdiot<FortunePosition, IdiotAsFortunePosition>(TextColor.color(0x87cefa), "占い師", playerNameFormatter)

    fun formatMedium(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsMediumPosition>(it, TextColor.color(0xff00ff)) }
    ): Component = formatPositionWithIdiot<MediumPosition, IdiotAsMediumPosition>(TextColor.color(0xff00ff), "霊媒師", playerNameFormatter)

    fun formatKnight(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameWithIdiotFormatter<IdiotAsKnightPosition>(it, TextColor.color(0x00ff7f)) }
    ): Component = formatPositionWithIdiot<KnightPosition, IdiotAsKnightPosition>(TextColor.color(0x00ff7f), "騎士", playerNameFormatter)

    fun formatFox(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, NamedTextColor.DARK_PURPLE) }
    ): Component = formatPosition<FoxPosition>(NamedTextColor.DARK_PURPLE, "妖狐", playerNameFormatter)

    fun formatCitizen(
        playerNameFormatter: (Participant) -> Component = { defaultPlayerNameComponent(it, NamedTextColor.BLUE) }
    ): Component = formatPosition<CitizenPosition>(NamedTextColor.BLUE, "村人", playerNameFormatter)


    private inline fun <reified T: Position> formatPosition(positionColor: TextColor, description: String, noinline formatter: (Participant) -> Component): Component {
        val positionLine = positionLineComponent(participants.filter { it.position is T }, formatter)

        return Component.text("《 $description 》", positionColor)
            .decorate(TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("  "))
            .append(positionLine)
    }

    private inline fun <reified T: Position, reified I: IdiotPosition> formatPositionWithIdiot(positionColor: TextColor, description: String, noinline formatter: (Participant) -> Component): Component {
        val idiotsAsFortune = participants.filter { it.position is I }

        val componentFortune = formatPosition<T>(positionColor, description, formatter)
        val componentIdiot = positionLineComponent(idiotsAsFortune, formatter)

        return if (componentIdiot == Component.empty())
            componentFortune
        else
            componentFortune
                .append(Component.text(", ", NamedTextColor.GRAY))
                .append(componentIdiot)
    }

    private fun positionLineComponent(participants: List<Participant>, formatter: (Participant) -> Component): Component =
        participants.map { formatter(it) }
            .reduceOrNull { acc, comp ->
                acc
                    .append(Component.text(", ", NamedTextColor.GRAY))
                    .append(comp)
            } ?: Component.empty()

    private fun defaultPlayerNameComponent(participant: Participant, textColor: TextColor): Component {
        val playerName = participant.bukkitPlayerProvider()?.name ?: nameCache.get(participant.uniqueId) ?: "unknownPlayer"

        return Component.text(playerName, textColor)
            .decorate(TextDecoration.BOLD)
    }

    private inline fun <reified I: IdiotPosition> defaultPlayerNameWithIdiotFormatter(participant: Participant, positionColor: TextColor): Component {
        if (participant.position is I) {
            return defaultPlayerNameComponent(participant, positionColor)
                .append(Component.text("(バカ)")
                    .color(positionColor)
                    .decorate(TextDecoration.BOLD)
                )
        }

        return defaultPlayerNameComponent(participant, positionColor)
    }
}