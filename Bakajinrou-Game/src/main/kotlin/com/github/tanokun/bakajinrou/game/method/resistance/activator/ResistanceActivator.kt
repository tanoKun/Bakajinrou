package com.github.tanokun.bakajinrou.game.method.resistance.activator

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protect.method.ResistanceMethod
import com.github.tanokun.bakajinrou.game.protect.ProtectVerificatorProvider
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * 防御手段「耐性」を有効化し、一定時間後に無効化します。
 *
 * @property game 現在のゲーム
 * @property provider 防御手段の有効性を検証するインスタンスを提供するためのProvider
 */
class ResistanceActivator(
    private val game: JinrouGame, private val provider: ProtectVerificatorProvider
) {

    /**
     * 指定された参加者に対して、耐性能力を20秒間有効化します。
     *
     * @param method 有効化する対象の[ResistanceMethod]
     * @param participantId 能力を使用する参加者のId
     */
    suspend fun activate(method: ResistanceMethod, participantId: ParticipantId) {
        val valid = method.copy(verificator = provider.getResistanceVerificator(isValid = true))

        game.updateParticipant(participantId) { current ->
            current
                .removeMethod(method)
                .grantMethod(valid)
        }

        delay(20.seconds)

        if (game.getParticipant(participantId)?.hasGrantedMethod(method.methodId) != false) return
        game.updateParticipant(participantId) { current ->
            current.removeMethod(valid)
        }
    }
}