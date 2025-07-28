package com.github.tanokun.bakajinrou.plugin.method

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

val itemKey: NamespacedKey = NamespacedKey("jinrou", "item")

interface AsBukkitItem: GrantedMethod {
    val transportable: Boolean

    fun createBukkitItem(): ItemStack
}

fun Participant.getGrantedMethodByItemStack(item: ItemStack): GrantedMethod? {
    val raw = item.persistentDataContainer.getOrDefault(itemKey, PersistentDataType.STRING, "")
    if (raw == "") return null

    return this.getGrantedMethod(UUID.fromString(raw))
}