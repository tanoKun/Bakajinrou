package com.github.tanokun.bakajinrou.game.participant.observer.initialization

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.ParticipantFilter
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

/**
 * 指定した条件の参加者に対して、自動的に初期化処理を実行するクラス。
 *
 * ゲーム開始時に、フィルターを満たす者に対して初期化が行われます。
 * たとえば「能力者のみ初期化」など、役職や生存者に応じた絞り込みが可能です。
 *
 * ## 注意
 * - フィルターに一致しない参加者には処理されません。
 * - 同一役職を複数収集をすると、多重初期化の可能性があります。
 */
abstract class ParticipantInitializer(jinrouGame: JinrouGame, gameController: JinrouGameSession, filter: ParticipantFilter): Observer {
    init {
        gameController.mainDispatcherScope.launch {
            gameController.observeParticipantAtLaunched()
                .mapNotNull { jinrouGame.getParticipant(it) }
                .filter(filter)
                .collect {
                    launch { initialize(it.participantId, jinrouGame.getCurrentParticipants()) }
                }
        }
    }

    /**
     * 対象の参加者に対して初期化処理を行います。
     *
     * @param selfId フィルターで指定した職業の参加者Id
     * @param participants 自分を含めた、全ての参加者
     */
    abstract suspend fun initialize(selfId: ParticipantId, participants: ParticipantScope)
}