package com.github.tanokun.bakajinrou.api.participant.position.citizen

import com.github.tanokun.bakajinrou.api.ability.ResultSource
import com.github.tanokun.bakajinrou.api.participant.position.Position

/**
 * 市民陣営を表すポジション。
 */
abstract class CitizensPosition: Position {
    override val abilityResult: ResultSource = ResultSource.CITIZENS
}