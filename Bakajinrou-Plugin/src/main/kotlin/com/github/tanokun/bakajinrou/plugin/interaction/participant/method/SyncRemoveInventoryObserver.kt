package com.github.tanokun.bakajinrou.plugin.interaction.participant.method

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.plugin.common.bukkit.item.ItemPersistent.getRawUuid
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 特定の手段が付与されたときに、プレイヤーのインベントリと同期します。
 *
 * 対象となる手段（MethodAssetKeys）が追加されると、自動的に同期処理を開始します。
 * CRAFTING が理由の場合は、クラフト状況に応じて追加方法を変えます。
 * また、同期前に同じ手段が削除されると、その処理は中断されます。
 *
 * 注意点:
 * - 同期はサスペンドで行われ、オンライン状態になるまで待機します。
 * - 不正なクラフトアイテムの場合、タイムアウトする場合があります。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SyncRemoveInventoryObserver(
    private val grantedStrategiesPublisher: GrantedStrategiesPublisher,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
    private val logger: DebugLogger,
): Observer {
    init {
        mainScope.launch {
            grantedStrategiesPublisher.observeDifference()
                .filterIsInstance<MethodDifference.Removed>()
                .collect(::syncInventory)
        }
    }

    /**
     * 削除された手段を、インベントリと同期します。
     */
    @OptIn(FlowPreview::class)
    private fun syncInventory(remove: MethodDifference.Removed) {
        logger.logRemoveMethod(remove.participantId, remove)

        mainScope.launch {
            val player = playerProvider.waitPlayerOnline(remove.participantId)
            val methodId = remove.removedMethod.methodId

            val item = player.inventory.contents.firstOrNull { it?.getRawUuid() == methodId.uniqueId } ?: return@launch
            player.inventory.removeItemAnySlot(item)
        }
    }
}