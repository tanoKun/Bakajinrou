package com.github.tanokun.bakajinrou.plugin.participant.method.attack.adapting

import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.plugin.common.item.ItemPersistent
import org.bukkit.entity.AbstractArrow
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

internal fun AbstractArrow.setMethodId(methodId: MethodId) {
    persistentDataContainer.set(
        ItemPersistent.JINROU_ITEM_MAKER,
        PersistentDataType.STRING,
        methodId.uniqueId.toString(),
    )
}

internal fun AbstractArrow.getMethodId(): MethodId? {
    val rawId = persistentDataContainer.get(
        ItemPersistent.JINROU_ITEM_MAKER,
        PersistentDataType.STRING,
    ) ?: return null

    return runCatching { UUID.fromString(rawId).asMethodId() }.getOrNull()
}
