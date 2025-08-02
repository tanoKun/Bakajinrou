package com.github.tanokun.bakajinrou.plugin.participant.position

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.text

sealed class Positions(
    val displayName: String,
    val color: TextColor
) {
    object Wolf: Positions("人狼", NamedTextColor.DARK_RED)
    object Madman: Positions("狂人", NamedTextColor.RED)
    object Citizen: Positions("市民", NamedTextColor.BLUE)
    object Fortune: Positions("占い師", TextColor.color(0x87cefa))
    object Medium: Positions("霊媒師", TextColor.color(0xff00ff))
    object Knight: Positions("騎士", TextColor.color(0x00ff7f))
    object Idiot: Positions("バカ", NamedTextColor.WHITE)
    object Fox: Positions("妖狐", NamedTextColor.DARK_PURPLE)
    object Spectator: Positions("観戦", NamedTextColor.AQUA)

    fun createDisplayComponent() = component { text(displayName) color color.asHexString() }
}