package com.github.tanokun.bakajinrou.game.ability.knight

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.ability.ProtectAbility
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.protection.ProtectVerificatorProvider
import com.github.tanokun.bakajinrou.game.state.GameTransition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.*

class ProtectAbilityExecutor(private val game: GameStore, private val provider: ProtectVerificatorProvider) {
    private val _protect = MutableSharedFlow<GrantProtectResult>()

    /**
     * 指定された騎士が、対象者に加護を付与します。
     *
     * 騎士からその能力を削除して状態を更新します。また、その結果を通知します。
     *
     * @param ability 使用される霊媒能力
     * @param knightId 加護を行う参加者のId
     * @param targetId 加護の対象となる参加者のId
     */
    suspend fun protect(ability: ProtectAbility, knightId: ParticipantId, targetId: ParticipantId) {
        val result = game.transact { currentGame ->
            val result = protectResult(currentGame.existParticipant(knightId), currentGame.existParticipant(targetId), knightId, targetId)

            if (result !is GrantProtectResult.Granted) {
                return@transact GameTransition(currentGame, result)
            }

            val methodId = UUID.randomUUID().asMethodId()
            val withoutAbility = currentGame.updateParticipant(knightId) { it.removeMethod(ability) }
            val updated = withoutAbility.updateParticipant(targetId) { current ->
                current.grantMethod(
                    ability.protect(provider.getTotemVerificator(targetId, methodId), methodId)
                )
            }

            GameTransition(updated, result)
        }

        if (result !is GrantProtectResult.Granted) return
        _protect.emit(result)
    }

    private fun protectResult(
        existsKnight: Boolean,
        existsTarget: Boolean,
        knightId: ParticipantId,
        targetId: ParticipantId,
    ): GrantProtectResult {
        if (!existsKnight || !existsTarget) return GrantProtectResult.NotFoundError(knightId, targetId)

        return GrantProtectResult.Granted(knightId, targetId)
    }

    fun observeProtect(): Flow<GrantProtectResult> = _protect.asSharedFlow()
}
