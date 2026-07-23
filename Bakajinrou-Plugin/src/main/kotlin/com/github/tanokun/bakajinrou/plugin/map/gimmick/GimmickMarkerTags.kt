package com.github.tanokun.bakajinrou.plugin.map.gimmick

object GimmickMarkerTags {
    const val ALTAR = "jinrou_gimmick_altar"
    const val OVERLOOK_ENTRY = "jinrou_gimmick_overlook_entry"
    const val OVERLOOK_DESTINATION = "jinrou_gimmick_overlook_destination"
    const val MERCHANT = "jinrou_gimmick_merchant"
    const val BOTANICAL_MERCHANT = "jinrou_gimmick_botanical_merchant"

    const val TRANSFER_GATE = "jinrou_gimmick_transfer_gate"
    const val TRANSFER_GATE_ALL = "jinrou_gimmick_transfer_gate_all"
    const val TRANSFER_GATE_FOX_ONLY = "jinrou_gimmick_transfer_gate_fox_only"

    private val legacyAliases = mapOf(
        TRANSFER_GATE to setOf("4_tp"),
        TRANSFER_GATE_ALL to setOf("4_all"),
        TRANSFER_GATE_FOX_ONLY to setOf("4_foxOnly"),
    )

    fun allNames(tag: String): Set<String> = setOf(tag) + legacyAliases[tag].orEmpty()

    fun contains(scoreboardTags: Set<String>, tag: String): Boolean =
        allNames(tag).any(scoreboardTags::contains)
}
