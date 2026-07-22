package com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack.item

enum class HijackFloor(
    val slot: Int,
    val displayName: String,
    val yRange: IntRange,
) {
    FIRST(15, "1F", 8..13),
    SECOND(16, "2F", 13..21),
    ROOF(17, "RF", 25..31),
}
