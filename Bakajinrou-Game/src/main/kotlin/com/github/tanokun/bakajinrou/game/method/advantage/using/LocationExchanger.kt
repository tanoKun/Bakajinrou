package com.github.tanokun.bakajinrou.game.method.advantage.using

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.advantage.ExchangeMethod
import com.github.tanokun.bakajinrou.api.advantage.using.ExchangeSelector
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class LocationExchanger(private val game: JinrouGame, private val selector: ExchangeSelector) {
    private val _exchanging = MutableSharedFlow<ExchangeInfo>(replay = 1)

    /**
     * 指定された参加者の位置を、ランダムに選定された別の参加者と交換します。
     * また、能力使用者から、使用した交換手段([method])を削除し、状態を更新し、
     * フローに流します。
     *
     * @param method 使用された交換手段
     * @param sideId 能力を使用した参加者のId
     */
    suspend fun exchange(method: ExchangeMethod, sideId: ParticipantId) {
        if (!game.existParticipant(sideId)) return

        val targetId = selector.select(sideId, game.getCurrentParticipants().excludeSpectators())

        game.updateParticipant(sideId) { current ->
            current.removeMethod(method)
        }

        _exchanging.emit(ExchangeInfo(sideId, targetId))
    }

    /**
     * 位置交換のストリームを購読するためのFlowを公開します。
     *
     * @param scope Flowの共有を管理するためのコルーチンスコープ
     * @return 位置交換情報 [ExchangeInfo] を放出するFlow
     */
    fun observeExchanging(scope: CoroutineScope): Flow<ExchangeInfo> = _exchanging.shareIn(scope, SharingStarted.Eagerly, replay = 1)
}