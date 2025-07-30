package com.github.tanokun.bakajinrou.plugin.formatter.display

import com.github.tanokun.bakajinrou.plugin.formatter.Positions
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class ComingOut(val displayName: String, val color: TextColor) {
    LAST_WOLF("ラスト人狼", NamedTextColor.DARK_RED),
    FORTUNE("占い師", Positions.Fortune.color),
    MEDIUM("霊媒師", Positions.Medium.color),
    KNIGHT("騎士", Positions.Knight.color),
}