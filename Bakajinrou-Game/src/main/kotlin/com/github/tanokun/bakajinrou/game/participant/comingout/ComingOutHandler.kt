package com.github.tanokun.bakajinrou.game.participant.comingout

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.prefix.ComingOut

/**
 * 参加者のカミングアウト操作を処理するハンドラークラスです。
 *
 * @param game 操作対象となるゲーム
 */
class ComingOutHandler(private val game: JinrouGame) {

    /**
     * 指定された参加者のカミングアウトの状態を更新します。
     *
     * 参加者がゲームに存在しない場合は、何も処理を行いません。
     *
     * @param participantId カミングアウトを行う参加者の [ParticipantId]
     * @param comingOut 設定するカミングアウト情報。`null` の場合は取り消し
     */
    suspend fun comingOut(participantId: ParticipantId, comingOut: ComingOut?) {
        if (!game.existParticipant(participantId)) return

        game.updateParticipant(participantId) { current ->
            current.comingOut(comingOut)
        }
    }
}