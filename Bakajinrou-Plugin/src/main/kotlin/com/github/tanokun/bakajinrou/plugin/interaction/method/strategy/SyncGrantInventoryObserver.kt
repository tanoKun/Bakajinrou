package com.github.tanokun.bakajinrou.plugin.interaction.method.strategy

import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translate.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingInfo
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.seconds

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
abstract class SyncGrantInventoryObserver(
    private val grantedStrategiesPublisher: GrantedStrategiesPublisher,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
    private val crafting: Crafting,
    private val logger: DebugLogger,
    vararg assetKey: MethodAssetKeys
): Observer {
    init {
        mainScope.launch {
            grantedStrategiesPublisher.observeDifference()
                .filterIsInstance<MethodDifference.Granted>()
                .filter { assetKey.contains(it.grantedMethod.assetKey) }
                .collect(::syncInventory)
        }
    }

    /**
     * 付与された手段に基づき、プレイヤーのインベントリを同期します。
     *
     * - CRAFTING の場合はクラフティング情報を取得し、適切な同期を行います。
     * - それ以外は通常の方法でアイテムを追加します。
     * - 同じ手段が削除されると中断されます。
     */
    @OptIn(FlowPreview::class)
    private fun syncInventory(add: MethodDifference.Granted) = mainScope.launch main@ {
        launch { uselessMethodObserver(this, add) }

        val asyncPlayer = async { playerProvider.waitPlayerOnline(add.participantId) }

        if (add.grantedMethod.reason == GrantedReason.CRAFTING) {
            val asyncCraftingInfo = async {
                crafting.observeCrafting(this@main)
                    .filter { it.method == add.grantedMethod }
                    .timeout(10.seconds)
                    .first()
            }.apply {
                invokeOnCompletion {
                    if (it is TimeoutCancellationException) this@main.cancel()
                }
            }

            val player = asyncPlayer.await()
            val craftingInfo = asyncCraftingInfo.await()

            syncInventoryOnCrafting(player, craftingInfo, add)
            logger.logAddMethod(add.participantId, add)

            this@main.cancel()
            return@main
        }

        val player = asyncPlayer.await()

        syncInventoryOnNormal(player, add)
        logger.logAddMethod(add.participantId, add)
        this.cancel()
    }

    /**
     * CRAFTING 理由で付与された手段を持つプレイヤーのインベントリを同期します。
     *
     * スタイルが [com.github.tanokun.bakajinrou.game.crafting.CraftingStyle.BULK] の場合は通常の追加処理を行い、
     * [com.github.tanokun.bakajinrou.game.crafting.CraftingStyle.SINGLE] の場合はカーソル上にアイテムをセットします。
     *
     * @param player 対象プレイヤー
     * @param craftingInfo クラフティングの詳細情報
     * @param add 付与された手段の差分情報
     */
    private fun syncInventoryOnCrafting(player: Player, craftingInfo: CraftingInfo, add: MethodDifference.Granted) {
        val style = craftingInfo.style

        when (style) {
            CraftingStyle.BULK -> {
                syncInventoryOnNormal(player, add)
            }
            CraftingStyle.SINGLE -> {
                player.setItemOnCursor(createItem(player, add))
            }
        }
    }

    /**
     * 通常理由で付与された手段を持つプレイヤーのインベントリを同期します。
     *
     * 表示用アイテムを作成し、プレイヤーのインベントリに追加します。
     *
     * @param player 対象プレイヤー
     * @param add 付与された手段の差分情報
     */
    private fun syncInventoryOnNormal(player: Player, add: MethodDifference.Granted) {
        val item = createItem(player, add)

        player.inventory.addItem(item)
    }

    /**
     * 付与された手段が削除されたかどうかを監視します。
     *
     * 同じ手段が削除された場合、関連する同期処理スコープをキャンセルします。
     *
     * @param scope 収集後キャンセルされるスコープ
     * @param add 付与された手段の差分情報
     */
    private suspend fun uselessMethodObserver(scope: CoroutineScope, add: MethodDifference.Granted) = grantedStrategiesPublisher.observeDifference()
        .filterIsInstance<MethodDifference.Removed>()
        .collect {
            if (it.removedMethod != add.grantedMethod) return@collect
            scope.cancel()
        }


    /**
     * 表示上のアイテムを作成します。
     *
     * 再開される前に同じ手段が削除された場合、追加処理は行われません。
     *
     * @param player 対象プレイヤー
     * @param add 追加された手段の差分情報
     *
     * @return 表示用のアイテム
     */
    abstract fun createItem(player: Player, add: MethodDifference.Granted): ItemStack
}