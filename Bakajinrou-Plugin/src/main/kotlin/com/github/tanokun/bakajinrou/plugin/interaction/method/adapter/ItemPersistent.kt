package com.github.tanokun.bakajinrou.plugin.interaction.method.adapter

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemViewer.isTransportable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

/**
 * アイテムのメタデータ操作や翻訳表示を行うためのユーティリティ。
 *
 * 手段IDや表示設定を PersistentDataContainer に保持し、
 * アイテムの識別や表示制御を可能にします。
 */
object ItemPersistent {
    val JINROU_ITEM_MAKER = NamespacedKey("jinrou", "maker")
    val IS_VISIBLE = NamespacedKey("jinrou", "visible")
    val IS_TRANSPORTABLE = NamespacedKey("jinrou", "transportable")

    /**
     * アイテムに手段情報と表示・ドロップ可否のメタデータを設定します。
     *
     * @receiver 設定対象の [ItemStack]
     * @param method 手段
     * @param isVisible アイテムが可視であるかどうか
     * @param isTransportable アイテムをドロップ可能かどうか
     */
    fun ItemStack.setMetadata(method: GrantedMethod, isVisible: Boolean) {
        this.editMeta {
            it.persistentDataContainer.set(JINROU_ITEM_MAKER, PersistentDataType.STRING, method.methodId.uniqueId.toString())
            it.persistentDataContainer.set(IS_VISIBLE, PersistentDataType.BOOLEAN, isVisible)
            it.persistentDataContainer.set(IS_TRANSPORTABLE, PersistentDataType.BOOLEAN, method.transportable)
        }
    }

    /**
     * アイテムから手段の UUID を取得します。
     *
     * @receiver 取得対象の [ItemStack]
     * @return 手段の UUID、存在しない場合は null
     */
    fun ItemStack.getRawUuid(): UUID? {
        if (!this.persistentDataContainer.has(JINROU_ITEM_MAKER)) return null

        val maker = this.persistentDataContainer.get(JINROU_ITEM_MAKER, PersistentDataType.STRING)
        return UUID.fromString(maker)
    }

    fun ItemStack.getMethodId(): MethodId? = getRawUuid()?.asMethodId()
}