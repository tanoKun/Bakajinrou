package com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class HijackItems(private val itemKey: NamespacedKey) {
    val floorsBySlot = HijackFloor.entries.associateBy(HijackFloor::slot)

    fun create(floor: HijackFloor) = ItemStack(Material.SLIME_BALL).apply {
        editMeta { meta ->
            meta.displayName(Component.text("ハイジャック(${floor.displayName})", NamedTextColor.RED))
            meta.persistentDataContainer.set(itemKey, PersistentDataType.STRING, floor.name)
        }
    }

    fun floorOf(item: ItemStack?): HijackFloor? {
        val floorId = item?.itemMeta?.persistentDataContainer
            ?.get(itemKey, PersistentDataType.STRING)
            ?: return null

        return HijackFloor.entries.find { it.name == floorId }
    }
}
