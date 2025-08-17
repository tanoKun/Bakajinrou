package com.github.tanokun.bakajinrou.game.participant.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession

/**
 * 参加者の初期化処理の一環として、役職固有の能力を付与するクラスです。
 *
 * [ParticipantInitializer] を実装し、参加者がゲームに参加した際に
 * その [com.github.tanokun.bakajinrou.api.participant.position.Position] が本来持っている手段
 * を自動的に付与します。
 *
 * @param game 操作対象となるゲーム
 * @param gameController ゲームセッション
 */
class InherentMethodsInitializer(
    private val game: JinrouGame, gameController: JinrouGameSession
) : ParticipantInitializer(game, gameController, { true }) {

    /**
     * 参加者に対し、その役職が持つ固有の手段をすべて付与します。
     *
     * 参加者が見つからない場合は、何もせずに処理を終了します。
     *
     * @param selfId 初期化対象となる参加者の [ParticipantId]
     */
    override suspend fun initialize(selfId: ParticipantId) {
        val self = game.getParticipant(selfId) ?: return

        val methods = self.position.inherentMethods()

        game.updateParticipant(selfId) { current ->
            methods.fold(current) { self, method -> self.grantMethod(method) }
        }
    }
}