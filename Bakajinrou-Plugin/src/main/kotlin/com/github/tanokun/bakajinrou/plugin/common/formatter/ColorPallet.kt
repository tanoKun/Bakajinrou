package com.github.tanokun.bakajinrou.plugin.common.formatter

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.format.TextColor

@Serializable
data class ColorPallet(val colors: HashMap<String, Int>) {

    fun getColor(key: String): TextColor = TextColor.color(colors.get(key) ?: 0)
}