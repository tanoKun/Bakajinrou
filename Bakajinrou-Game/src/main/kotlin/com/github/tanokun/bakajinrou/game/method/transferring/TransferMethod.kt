package com.github.tanokun.bakajinrou.game.method.transferring

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.protection.method.ProtectiveMethod


/**
 * 参加者間での手段譲渡を表現します。
 *
 * このクラスは、ある参加者が持つ手段を別の参加者に移動させる一連のプロセスを調整します。
 * 状態の読み込みと変更は、コンストラクタで渡される [com.github.tanokun.bakajinrou.api.JinrouGame] インスタンスを介して行われます。
 *
 * @property game 現在のゲーム
 */
class TransferMethod(private val game: JinrouGame) {

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
        val from = game.getParticipant(fromId) ?: return false
        if (!game.existParticipant(toId)) return false

        val origin = from.getGrantedMethod(methodId) ?: return false

        val grantMethod = if (origin is ProtectiveMethod) origin.asTransferred(toId) else origin.asTransferred()

        game.updateParticipant(fromId) { current -> current.removeMethod(origin) }
        game.updateParticipant(toId) { current -> current.grantMethod(grantMethod) }

        return true
    }
}