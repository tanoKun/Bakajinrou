package com.github.tanokun.bakajinrou.plugin.participant.method

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.game.state.GameChanges
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.game.crafting.Crafting
import com.github.tanokun.bakajinrou.game.crafting.CraftingInfo
import com.github.tanokun.bakajinrou.game.crafting.CraftingStyle
import com.github.tanokun.bakajinrou.plugin.common.item.ItemPersistent.setMetadata
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.presentation.item.MethodItemPresenter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.seconds

/**
 * 手段が付与されたときに、プレイヤーのインベントリと同期します。
 *
 * [MethodAssetKeys] と表示用アイテムを結び付け、追加されたすべての手段を同期します。
 * CRAFTING が理由の場合は、クラフト状況に応じて追加方法を変えます。
 * また、同期前に同じ手段が削除されると、その処理は中断されます。
 *
 * 注意点:
 * - 同期はサスペンドで行われ、オンライン状態になるまで待機します。
 * - 不正なクラフトアイテムの場合、タイムアウトする場合があります。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GrantedInventorySynchronizer(
    private val gameChanges: GameChanges,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
    private val crafting: Crafting,
    private val methodItemPresenter: MethodItemPresenter,
): Observer {
    init {
        mainScope.launch {
            gameChanges.methodChanges
                .filterIsInstance<MethodDifference.Granted>()
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
    private fun syncInventory(granted: MethodDifference.Granted) = mainScope.launch main@ {
        launch { uselessMethodObserver(this, granted) }

        val asyncPlayer = async { playerProvider.waitPlayerOnline(granted.participantId) }

        if (granted.grantedMethod.reason == GrantedReason.CRAFTED) {
            val asyncCraftingInfo = async {
                crafting.observeCrafting()
                    .filter { it.crafterId == granted.participantId }
                    .filter { it.method == granted.grantedMethod }
                    .timeout(10.seconds)
                    .first()
            }.apply {
                invokeOnCompletion {
                    if (it is TimeoutCancellationException) this@main.cancel()
                }
            }

            val player = asyncPlayer.await()
            val craftingInfo = asyncCraftingInfo.await()

            syncInventoryOnCrafting(player, craftingInfo, granted)

            this@main.cancel()
            return@main
        }

        val player = asyncPlayer.await()

        syncInventoryOnNormal(player, granted)
        this.cancel()
    }

    /**
     * CRAFTING 理由で付与された手段を持つプレイヤーのインベントリを同期します。
     *
     * スタイルが [CraftingStyle.BULK] の場合は通常の追加処理を行い、
     * [CraftingStyle.SINGLE] の場合はカーソル上にアイテムをセットします。
     *
     * @param player 対象プレイヤー
     * @param craftingInfo クラフティングの詳細情報
     * @param granted 付与された手段の差分情報
     */
    private fun syncInventoryOnCrafting(player: Player, craftingInfo: CraftingInfo, granted: MethodDifference.Granted) {
        val style = craftingInfo.style

        when (style) {
            CraftingStyle.BULK -> {
                syncInventoryOnNormal(player, granted)
            }
            CraftingStyle.SINGLE -> {
                player.setItemOnCursor(createItem(player, granted))
            }
        }
    }

    /**
     * 通常理由で付与された手段を持つプレイヤーのインベントリを同期します。
     *
     * 表示用アイテムを作成し、プレイヤーのインベントリに追加します。
     *
     * @param player 対象プレイヤー
     * @param granted 付与された手段の差分情報
     */
    private fun syncInventoryOnNormal(player: Player, granted: MethodDifference.Granted) {
        val item = createItem(player, granted)

        player.inventory.addItem(item)
    }

    /**
     * 付与された手段が削除されたかどうかを監視します。
     *
     * 同じ手段が削除された場合、関連する同期処理スコープをキャンセルします。
     *
     * @param scope 収集後キャンセルされるスコープ
     * @param granted 付与された手段の差分情報
     */
    private suspend fun uselessMethodObserver(scope: CoroutineScope, granted: MethodDifference.Granted) = gameChanges.methodChanges
        .filterIsInstance<MethodDifference.Removed>()
        .collect {
            if (it.removedMethod != granted.grantedMethod) return@collect
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
    private fun createItem(player: Player, add: MethodDifference.Granted): ItemStack {
        val assetKey = add.grantedMethod.assetKey
        val locale = player.locale()
        val item = when (assetKey) {
            MethodAssetKeys.Attack.GAS -> methodItemPresenter.gas(locale)
            MethodAssetKeys.Attack.SWORD -> methodItemPresenter.sword(locale)
            MethodAssetKeys.Attack.ARROW -> methodItemPresenter.arrow(locale)
            MethodAssetKeys.Attack.SCATTER_CROSSBOW -> methodItemPresenter.scatterCrossbow(locale)
            MethodAssetKeys.Protective.TOTEM -> methodItemPresenter.totem(locale)
            MethodAssetKeys.Protective.FAKE_TOTEM -> methodItemPresenter.fakeTotem(locale)
            MethodAssetKeys.Protective.SHIELD -> methodItemPresenter.shield(locale)
            MethodAssetKeys.Protective.RESISTANCE -> methodItemPresenter.resistance(locale)
            MethodAssetKeys.Advantage.EXCHANGE -> methodItemPresenter.exchange(locale)
            MethodAssetKeys.Advantage.SPEED -> methodItemPresenter.speed(locale)
            MethodAssetKeys.Advantage.INVISIBILITY -> methodItemPresenter.invisibility(locale)
            MethodAssetKeys.Ability.DIVINE -> methodItemPresenter.divine(locale)
            MethodAssetKeys.Ability.COMMUNE -> methodItemPresenter.commune(locale)
            MethodAssetKeys.Ability.PROTECT -> methodItemPresenter.protect(locale)
            else -> error("未対応の手段表示です: ${assetKey.key}")
        }

        return item.apply {
            setMetadata(add.grantedMethod, isVisible = assetKey !is MethodAssetKeys.Ability)
        }
    }
}
