package com.github.tanokun.bakajinrou.api.participant.strategy

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * プレイヤーに付与されている手段の変化を監視し、
 * 手段の追加・削除といった差分を検出するためのハンドラークラスです。
 *
 * UI 表示やログ、通知などにご活用いただけます。
 *
 * @property jinrouGame ゲームの状態を保持しているインスタンス
 */
class GrantedStrategiesProvider(private val jinrouGame: JinrouGame) {

    /**
     * 参加者の所持手段に対する差分を監視します。
     *
     * プレイヤーの戦略が更新されるたびに、以前の状態との差分を比較し、
     * メソッドの追加や削除があった場合に [MethodDifference] として通知します。
     * 差分が存在しない場合は何も送信されません。
     *
     * @param scope 監視するコルーチンスコープ
     *
     * @return 差分を表す [MethodDifference] を流す [Flow] を返します。
     */
    fun observeDifference(scope: CoroutineScope): Flow<MethodDifference> = channelFlow {
        var previousStrategy: GrantedStrategy? = null

        jinrouGame.observeParticipants(scope).collect { participant ->
            val currentStrategy = participant.strategy

            getDifference(participant, previousStrategy, currentStrategy).forEach { diff -> send(diff) }

            previousStrategy = currentStrategy
        }
    }

    private fun getDifference(
        participant: Participant, previous: GrantedStrategy?, current: GrantedStrategy
    ): List<MethodDifference> {

        val previousMethods = previous?.strategies ?: mapOf()
        val currentMethods = current.strategies

        val addedKeys = currentMethods.keys - previousMethods.keys
        val removedKeys = previousMethods.keys - currentMethods.keys

        return addedKeys.mapNotNull {
            MethodDifference.MethodAdded(participant, currentMethods[it] ?: return@mapNotNull null)
        } + removedKeys.mapNotNull {
            MethodDifference.MethodRemoved(participant, previousMethods[it] ?: return@mapNotNull null)
        }
    }
}