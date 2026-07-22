package com.github.tanokun.bakajinrou.game.method.transferring

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protection.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.game.state.GameTransition


/**
 * 参加者間での手段譲渡を表現します。
 *
 * このクラスは、ある参加者が持つ手段を別の参加者に移動させる一連のプロセスを調整します。
 * 状態の読み込みと変更は、コンストラクタで渡される [GameStore] を介して行われます。
 *
 * @property game 現在のゲーム
 */
class TransferMethod(private val game: GameStore) {

    /**
     * 指定された手段を、ある参加者から別の参加者へ譲渡します。
     * また、以下の場合は譲渡せず処理を終了します。
     * - 譲渡元に [methodId] の手段を持たない場合
     *
     * @param methodId 譲渡対象の手段Id
     * @param fromId 譲渡元の参加者のId
     * @param toId 譲渡先の参加者のId
     *
     * @return 譲渡成功 true, 失敗 false
     */
    suspend fun transport(methodId: MethodId, fromId: ParticipantId, toId: ParticipantId): Boolean {
        return game.transact { currentGame ->
            val from = currentGame.getParticipant(fromId)
                ?: return@transact GameTransition(currentGame, false)
            if (!currentGame.existParticipant(toId)) {
                return@transact GameTransition(currentGame, false)
            }

            val origin = from.getGrantedMethod(methodId)
                ?: return@transact GameTransition(currentGame, false)

            val granted = if (origin is ProtectiveMethod) origin.asTransferred(toId) else origin.asTransferred()
            val removed = currentGame.updateParticipant(fromId) { it.removeMethod(origin) }
            val updated = removed.updateParticipant(toId) { it.grantMethod(granted) }

            GameTransition(updated, true)
        }
    }
}
