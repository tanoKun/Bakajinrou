package com.github.tanokun.bakajinrou.game.crafting

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.advantage.ExchangeMethod
import com.github.tanokun.bakajinrou.api.advantage.InvisibilityMethod
import com.github.tanokun.bakajinrou.api.advantage.SpeedMethod
import com.github.tanokun.bakajinrou.api.attacking.method.DamagePotionMethod
import com.github.tanokun.bakajinrou.api.attacking.method.SwordMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protection.method.ResistanceMethod
import com.github.tanokun.bakajinrou.api.protection.method.ShieldMethod
import com.github.tanokun.bakajinrou.game.protection.ProtectVerificatorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
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
    private val game: JinrouGame,
    private val random: Random,
    private val provider: ProtectVerificatorProvider,
) {

    private val _crafting = MutableSharedFlow<CraftingInfo>(replay = 1)

    /**
     * クラフトの購読を開始します。
     *
     * 複数の購読者に対しては、同一インスタンスが共有されます。
     *
     * @param scope この Flow を共有するスコープ
     *
     * @return クラフト情報の Flow
     */
    fun observeCrafting(scope: CoroutineScope): Flow<CraftingInfo> = _crafting.shareIn(scope, SharingStarted.Eagerly, replay = 1)

    private val crafting = listOf<(ParticipantId) -> GrantedMethod>(
        { SwordMethod(reason = GrantedReason.CRAFTED) },
        { DamagePotionMethod(reason = GrantedReason.CRAFTED) },
        { id -> ResistanceMethod(reason = GrantedReason.CRAFTED, verificator = provider.getResistanceVerificator(false)) },
        { id ->
            val methodId = UUID.randomUUID().asMethodId()
            ShieldMethod(methodId = methodId, reason = GrantedReason.CRAFTED, verificator = provider.getShieldVerificator(id, methodId))
        },
        { SpeedMethod(reason = GrantedReason.CRAFTED) },
        { InvisibilityMethod(reason = GrantedReason.CRAFTED) },
        { ExchangeMethod(reason = GrantedReason.CRAFTED) }
    )

    /**
     * 指定参加者に対して、ランダムに1つの手段が追加されます。
     *
     * @param participantId 対象参加者の Id
     * @param style クラフトのスタイル
     */
    suspend fun randomlyCraftMethod(participantId: ParticipantId, style: CraftingStyle) {
        if (!game.existParticipant(participantId)) return

        val method = crafting.random(random).invoke(participantId)

        game.updateParticipant(participantId) { current ->
            current.grantMethod(method)
        }

        _crafting.emit(CraftingInfo(participantId, style, method))
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