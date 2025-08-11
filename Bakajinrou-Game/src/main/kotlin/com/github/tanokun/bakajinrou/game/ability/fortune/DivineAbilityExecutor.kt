package com.github.tanokun.bakajinrou.game.ability.fortune

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.DivineAbility
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

/**
 * 占い能力の実行をします。
 *
 * このクラスは、占いに関連する一連のプロセスを担います。
 * - 占い能力消費による、参加者エンティティの状態更新
 * - 占い結果のイベント通知
 *
 * @property game 実行中のゲーム
 */
class DivineAbilityExecutor(private val game: JinrouGame) {
    private val _divine = MutableSharedFlow<DivineResult>()

    /**
     * 指定された占い能力を実行し、結果を内部Flowに発行します。
     *
     * 占いの結果が見つかった場合、能力を消費したとみなし、
     * 占い師からその能力を削除して状態を更新します。
     *
     * @param ability 使用される占い能力
     * @param fortuneId 占いを行う参加者のId
     * @param targetId 占いの対象となる参加者のId
     */
    suspend fun divine(ability: DivineAbility, fortuneId: ParticipantId, targetId: ParticipantId) {
        val result = divineResult(ability, fortuneId, targetId)

        if (result is DivineResult.FoundResult) {
            game.updateParticipant(fortuneId) { current ->
                current.removeMethod(ability)
            }
        }

        _divine.emit(result)
    }

    private fun divineResult(ability: DivineAbility, fortuneId: ParticipantId, targetId: ParticipantId): DivineResult {
        if (!game.existParticipant(fortuneId)) return DivineResult.NotFoundError(fortuneId, targetId)
        val target = game.getParticipant(targetId) ?: return DivineResult.NotFoundError(fortuneId, targetId)

        val result = ability.divine(target)

        return DivineResult.FoundResult(result, fortuneId, targetId)
    }

    fun observeDivine(scope: CoroutineScope): Flow<DivineResult> =
        _divine.shareIn(scope, SharingStarted.Companion.Eagerly, replay = 1)
}