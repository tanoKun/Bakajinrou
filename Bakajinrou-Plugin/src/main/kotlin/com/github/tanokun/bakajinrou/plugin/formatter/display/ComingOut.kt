package com.github.tanokun.bakajinrou.plugin.formatter.display

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class ComingOut(val displayName: String, val color: TextColor) {
    LAST_WOLF("ラスト人狼", NamedTextColor.DARK_RED),
    FORTUNE("占い師", TextColor.color(0x87cefa)),
    MEDIUM("霊媒師", TextColor.color(0xff00ff)),
    KNIGHT("騎士", TextColor.color(0x00ff7f)),
}