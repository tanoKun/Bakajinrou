package com.github.tanokun.bakajinrou.game.observer.initializer

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantFilter
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import kotlinx.coroutines.flow.filter
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
abstract class ParticipantInitializer(jinrouGame: JinrouGame, gameController: JinrouGameController, filter: ParticipantFilter) {
    init {
        gameController.mainDispatcherScope.launch {
            gameController.observeParticipantAtLaunched()
                .filter(filter)
                .collect {
                    initialize(it, jinrouGame.getAllParticipants())
                }
        }
    }

    /**
     * 対象の参加者に対して初期化処理を行います。
     *
     * @param self フィルターで指定した職業の参加者
     * @param participants 自分を含めた、全ての参加者
     */
    abstract fun initialize(self: Participant, participants: ParticipantScope)
}