package com.github.tanokun.bakajinrou.game.crafting

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.advantage.ExchangeMethod
import com.github.tanokun.bakajinrou.api.advantage.InvisibilityMethod
import com.github.tanokun.bakajinrou.api.advantage.SpeedMethod
import com.github.tanokun.bakajinrou.api.attacking.method.GasMethod
import com.github.tanokun.bakajinrou.api.attacking.method.ScatterCrossbowMethod
import com.github.tanokun.bakajinrou.api.attacking.method.SwordMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protection.method.ResistanceMethod
import com.github.tanokun.bakajinrou.api.protection.method.ShieldMethod
import com.github.tanokun.bakajinrou.game.protection.ProtectVerificatorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.*
import kotlin.random.Random

/**
 * クラフト処理に行うサービスです。
 *
 * また、クラフトイベントは購読可能で、外部に通知されます。
 *
 * @param game クラフト対象となる参加者を保持するゲームインスタンス
 * @param random 手段選択に使う乱数インスタンス
 */
class Crafting(
    private val game: GameStore,
    private val random: Random,
    private val provider: ProtectVerificatorProvider,
) {
    private val randomCraftingProducts = CraftingProduct.entries
        .filterNot { it == CraftingProduct.SCATTER_CROSSBOW }

    private val _crafting = MutableSharedFlow<CraftingInfo>(replay = 1)

    /**
     * クラフトの購読を開始します。
     *
     * 複数の購読者に対しては、同一インスタンスが共有されます。
     *
     * @return クラフト情報の Flow
     */
    fun observeCrafting(): Flow<CraftingInfo> = _crafting.asSharedFlow()

    /**
     * 指定参加者に対して、ランダムに1つの手段が追加されます。
     *
     * @param participantId 対象参加者の Id
     * @param style クラフトのスタイル
     */
    suspend fun randomlyCraftMethod(participantId: ParticipantId, style: CraftingStyle) {
        if (!game.existParticipant(participantId)) return

        val product = randomCraftingProducts.random(random)

        craftMethod(participantId, product, style)
    }

    /**
     * 指定した商品に対応する手段を参加者へ追加します。
     *
     * @return 追加した手段。参加者が存在しない場合は null
     */
    suspend fun craftMethod(
        participantId: ParticipantId,
        product: CraftingProduct,
        style: CraftingStyle,
    ): GrantedMethod? {
        if (!game.existParticipant(participantId)) return null

        val method = createMethod(product, participantId)

        game.updateParticipant(participantId) { current -> current.grantMethod(method) }
        _crafting.emit(CraftingInfo(participantId, style, method))

        return method
    }

    private fun createMethod(product: CraftingProduct, participantId: ParticipantId): GrantedMethod = when (product) {
        CraftingProduct.SWORD -> SwordMethod(reason = GrantedReason.CRAFTED)
        CraftingProduct.GAS -> GasMethod(reason = GrantedReason.CRAFTED)
        CraftingProduct.RESISTANCE -> ResistanceMethod(
            reason = GrantedReason.CRAFTED,
            verificator = provider.getResistanceVerificator(false),
        )
        CraftingProduct.SHIELD -> {
            val methodId = UUID.randomUUID().asMethodId()
            ShieldMethod(
                methodId = methodId,
                reason = GrantedReason.CRAFTED,
                verificator = provider.getShieldVerificator(participantId, methodId),
            )
        }
        CraftingProduct.SPEED -> SpeedMethod(reason = GrantedReason.CRAFTED)
        CraftingProduct.INVISIBILITY -> InvisibilityMethod(reason = GrantedReason.CRAFTED)
        CraftingProduct.EXCHANGE -> ExchangeMethod(reason = GrantedReason.CRAFTED)
        CraftingProduct.SCATTER_CROSSBOW -> ScatterCrossbowMethod(reason = GrantedReason.CRAFTED)
    }

    /**
     * 指定参加者が「固有の手段」を持っている場合、
     * それらをクラフト・追加します。
     *
     * @param participantId 対象参加者の Id
     * @param style クラフトのスタイル
     */
    suspend fun craftInherentMethods(participantId: ParticipantId, style: CraftingStyle) {
        val participant = game.getParticipant(participantId) ?: return

        val methods = participant.position.inherentMethods().map { it.asCrafted() }

        if (methods.isEmpty()) return

        game.updateParticipant(participantId) { current ->
            methods.fold(current) { acc, method ->
                acc.grantMethod(method)
            }
        }

        methods.forEach { method ->
            _crafting.emit(CraftingInfo(participantId, style, method))
        }
    }

    /**
     * 指定参加者が「固有の手段」を持っていることを確認します。
     *
     * @return 「固有の手段」を持っているか
     */
    fun hasInherentMethods(participantId: ParticipantId): Boolean {
        val participant = game.getParticipant(participantId) ?: return false

        return participant.position.inherentMethods().isNotEmpty()
    }
}
