package com.github.tanokun.bakajinrou.game.ability.knight

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ability.ProtectAbility
import com.github.tanokun.bakajinrou.api.method.asMethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.protection.ProtectVerificatorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import java.util.*

class ProtectAbilityExecutor(private val game: JinrouGame, private val provider: ProtectVerificatorProvider) {
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
        val result = protectResult(knightId, targetId)

        if (result !is GrantProtectResult.Granted) return

        game.updateParticipant(knightId) { current -> current.removeMethod(ability) }
        game.updateParticipant(targetId) { current ->
            val methodId = UUID.randomUUID().asMethodId()
            current.grantMethod(
                ability.protect(provider.getTotemVerificator(targetId, methodId), methodId)
            )
        }

        _protect.emit(result)
    }

    private fun protectResult(mediumId: ParticipantId, targetId: ParticipantId): GrantProtectResult {
        if (!game.existParticipant(mediumId)) return GrantProtectResult.NotFoundError(mediumId, targetId)
        if (!game.existParticipant(targetId)) return GrantProtectResult.NotFoundError(mediumId, targetId)

        return GrantProtectResult.Granted(mediumId, targetId)
    }

    fun observeProtect(scope: CoroutineScope): Flow<GrantProtectResult> =
        _protect.shareIn(scope, SharingStarted.Eagerly, replay = 1)
}