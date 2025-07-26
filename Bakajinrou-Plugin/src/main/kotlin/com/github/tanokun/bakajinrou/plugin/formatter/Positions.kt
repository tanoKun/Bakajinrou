package com.github.tanokun.bakajinrou.plugin.formatter

import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.SpectatorPosition
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.MadmanPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.position.SpectatorOtherPosition
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
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

private fun onlySpectators() = listOf(SpectatorOtherPosition)

sealed class Positions(
    val candidatePositions: List<Position>, val color: TextColor
) {
    object Wolf: Positions(listOf(WolfSecondPosition), NamedTextColor.DARK_RED)
    object Madman: Positions(listOf(MadmanSecondPosition), NamedTextColor.RED)
    object Citizen: Positions(listOf(FoxThirdPosition), NamedTextColor.BLUE)
    object Fortune: Positions(listOf(FortunePosition), TextColor.color(0x87cefa))
    object Medium: Positions(listOf(MediumPosition), TextColor.color(0xff00ff))
    object Knight: Positions(listOf(KnightPosition), TextColor.color(0x00ff7f))
    object Idiot: Positions(listOf(IdiotAsFortunePosition, IdiotAsMediumPosition, IdiotAsKnightPosition), NamedTextColor.WHITE)
    object Fox: Positions(listOf(FoxThirdPosition), NamedTextColor.DARK_PURPLE)
    object Spectator: Positions(onlySpectators(), NamedTextColor.AQUA)
}

fun getPositionColor(position: Position): TextColor = when (position) {
    is WolfPosition -> Positions.Wolf.color
    is MadmanPosition -> Positions.Madman.color
    is CitizenPosition -> Positions.Citizen.color
    is FortunePosition, is IdiotAsFortunePosition -> Positions.Fortune.color
    is MediumPosition, is IdiotAsMediumPosition -> Positions.Medium.color
    is KnightPosition, is IdiotAsKnightPosition -> Positions.Knight.color
    is FoxPosition -> Positions.Fox.color
    is SpectatorPosition -> Positions.Spectator.color
    else -> throw IllegalStateException("対応しない役職")
}