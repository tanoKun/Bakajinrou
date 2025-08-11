package com.github.tanokun.bakajinrou.game.ability.medium

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.CommuneAbility
import com.github.tanokun.bakajinrou.api.ability.medium.CommuneResultSource
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

/**
 * 霊媒能力の実行をします。
 *
 * このクラスは、霊媒に関連する一連のプロセスを担います。
 * - 霊媒能力消費による、参加者エンティティの状態更新
 * - 霊媒結果のイベント通知
 *
 * @property jinrouGame 実行中のゲーム
 */
class CommuneAbilityExecutor(private val jinrouGame: JinrouGame) {
    private val _commune = MutableSharedFlow<CommuneResult>()

    /**
     * 指定された霊媒能力を実行し、結果を内部Flowに発行します。
     *
     * 霊媒が成功した場合、能力を消費したとみなし、
     * 霊媒師からその能力を削除して状態を更新します。
     *
     * @param ability 使用される霊媒能力
     * @param mediumId 霊媒を行う参加者のId
     * @param targetId 霊媒の対象となる参加者のId
     */
    suspend fun commune(ability: CommuneAbility, mediumId: ParticipantId, targetId: ParticipantId) {
        val result = communeResult(ability, mediumId, targetId)

        if (result is CommuneResult.Success)
            jinrouGame.updateParticipant(mediumId) { current ->
                current.removeMethod(ability)
            }


        _commune.emit(result)
    }

    private fun communeResult(ability: CommuneAbility, mediumId: ParticipantId, targetId: ParticipantId): CommuneResult {
        if (!jinrouGame.existParticipant(mediumId)) return CommuneResult.NotFoundError(mediumId, targetId)
        val target = jinrouGame.getParticipant(targetId) ?: return CommuneResult.NotFoundError(mediumId, targetId)

        val result = ability.commune(target)

        return when (result) {
            is CommuneResultSource.FoundResult -> CommuneResult.FoundResult(result.resultKey, mediumId, targetId)
            CommuneResultSource.NotDeadError -> CommuneResult.IsNotDead(mediumId, targetId)
        }
    }

    fun observeCommune(scope: CoroutineScope): Flow<CommuneResult>  =
        _commune.shareIn(scope, SharingStarted.Eagerly, replay = 1)
}