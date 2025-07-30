package com.github.tanokun.bakajinrou.plugin.method

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*

val itemKey: NamespacedKey = NamespacedKey("jinrou", "item")
val isVisibleKey: NamespacedKey = NamespacedKey("jinrou", "is_visible")

interface AsBukkitItem: GrantedMethod {
    val transportable: Boolean

    /**
     * 他の参加者に、この手段を所持していることを秘匿します。
     * 観察者が以下の条件の場合、false でも表示されます。
     * - 死亡状態
     * - 観戦者
     */
    val isVisible: Boolean

    fun createBukkitItem(): ItemStack

    fun setPersistent(container: PersistentDataContainer) {
        container.set(itemKey, PersistentDataType.STRING, uniqueId.toString())
        container.set(isVisibleKey, PersistentDataType.BOOLEAN, isVisible)
    }
}

fun Participant.getGrantedMethodByItemStack(item: ItemStack): GrantedMethod? {
    val raw = item.persistentDataContainer.getOrDefault(itemKey, PersistentDataType.STRING, "")
    if (raw == "") return null

    return this.getGrantedMethod(UUID.fromString(raw))
}