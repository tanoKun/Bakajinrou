package com.github.tanokun.bakajinrou.plugin.setting.prepare.desided

import com.github.tanokun.bakajinrou.plugin.setting.prepare.command.MAP
import com.github.tanokun.bakajinrou.plugin.setting.prepare.command.PARTICIPANTS
import com.github.tanokun.bakajinrou.plugin.setting.prepare.command.POSITIONS
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import kotlinx.serialization.json.Json
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun getSelectedData(item: ItemStack): Triple<SelectedMap, SelectedParticipants, SelectedPositions>? {
    val jsonMap = item.persistentDataContainer.get(MAP, PersistentDataType.STRING) ?: return null
    val jsonParticipants = item.persistentDataContainer.get(PARTICIPANTS, PersistentDataType.STRING) ?: return null
    val jsonPositions = item.persistentDataContainer.get(POSITIONS, PersistentDataType.STRING) ?: return null

    val map = Json.decodeFromString<SelectedMap>(jsonMap)
    val participants = Json.decodeFromString<SelectedParticipants>(jsonParticipants)
    val positions = Json.decodeFromString<SelectedPositions>(jsonPositions)

    return Triple(map, participants, positions)
}