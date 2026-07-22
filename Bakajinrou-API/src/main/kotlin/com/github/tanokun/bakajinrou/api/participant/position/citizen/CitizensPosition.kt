package com.github.tanokun.bakajinrou.api.participant.position.citizen

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Side

/**
 * 市民陣営を表すポジション。
 */
abstract class CitizensPosition: Position {
    override val side: Side? = Side.VILLAGE

    override val divinedAs: ResultSource = ResultSource.CITIZENS
}