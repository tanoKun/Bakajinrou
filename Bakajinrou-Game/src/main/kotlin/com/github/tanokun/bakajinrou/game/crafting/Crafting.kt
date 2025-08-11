package com.github.tanokun.bakajinrou.game.crafting

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.advantage.ExchangeMethod
import com.github.tanokun.bakajinrou.api.advantage.InvisibilityMethod
import com.github.tanokun.bakajinrou.api.advantage.SpeedMethod
import com.github.tanokun.bakajinrou.api.attack.method.DamagePotionMethod
import com.github.tanokun.bakajinrou.api.attack.method.SwordMethod
import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protect.method.ResistanceMethod
import com.github.tanokun.bakajinrou.api.protect.method.ShieldMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlin.random.Random

/**
 * クラフト処理に行うサービスです。
 *
 * プレイヤーがクラフトを実行すると、事前に定義された方法の中から
 * ランダムに1つが選ばれ、その手段が参加者に追加されます。
 *
 * また、クラフトイベントは購読可能で、外部に通知されます。
 *
 * @param game クラフト対象となる参加者を保持するゲームインスタンス
 * @param random 手段選択に使う乱数インスタンス
 */
class Crafting(
    private val game: JinrouGame,
    private val random: Random
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

    private val crafting = listOf<(JinrouGame) -> GrantedMethod>(
        { SwordMethod(reason = GrantedReason.CRAFTING) },
        { DamagePotionMethod(reason = GrantedReason.CRAFTING) },
        { ResistanceMethod(reason = GrantedReason.CRAFTING) },
        { ShieldMethod(reason = GrantedReason.CRAFTING) },
        { SpeedMethod(reason = GrantedReason.CRAFTING) },
        { InvisibilityMethod(reason = GrantedReason.CRAFTING) },
        { ExchangeMethod(reason = GrantedReason.CRAFTING) }
    )

    /**
     * 指定参加者に対して、ランダムに1つの手段が追加されます。
     *
     * @param participantId 対象参加者の Id
     * @param style クラフトのスタイル
     */
    suspend fun randomlyCrafting(participantId: ParticipantId, style: CraftingStyle) {
        val participant = game.getParticipant(participantId) ?: return
        val method = crafting.random(random).invoke(game)

        game.updateParticipant(participantId) { current ->
            current.grantMethod(method)
        }

        _crafting.emit(CraftingInfo(participant, style, method))
    }
}