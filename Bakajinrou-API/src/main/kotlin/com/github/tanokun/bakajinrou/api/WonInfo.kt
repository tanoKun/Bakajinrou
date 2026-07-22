package com.github.tanokun.bakajinrou.api

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Side

/**
 * ゲームの終了結果を表します。
 *
 * @property participants 終了時点のすべての参加者
 */
sealed interface WonInfo {
    val participants: ParticipantScope.All

    /**
     * いずれかの陣営が勝利したことを表します。
     *
     * @property side 勝利した陣営
     */
    data class Won(val side: Side, override val participants: ParticipantScope.All) : WonInfo

    /**
     * システムによってゲームが強制終了されたことを表します。
     */
    data class System(override val participants: ParticipantScope.All) : WonInfo
}
