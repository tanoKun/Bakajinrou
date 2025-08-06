package com.github.tanokun.bakajinrou.game.ability

import com.github.tanokun.bakajinrou.api.ability.fortune.DivineResult
import com.github.tanokun.bakajinrou.api.ability.fortune.FortuneAbility
import com.github.tanokun.bakajinrou.api.participant.Participant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import java.util.*

class FortuneAbilityExecutor(private val fortuneAbility: FortuneAbility) {
    private val _divine = MutableSharedFlow<Participant>()

    fun divine(target: UUID): DivineResult {
        val result = fortuneAbility.divine(target)

        if (result is DivineResult.FoundResult) _divine.tryEmit(result.target)

        return fortuneAbility.divine(target)
    }

    fun observeDivine(scope: CoroutineScope): Flow<Participant>  =
        _divine.shareIn(scope, SharingStarted.Eagerly, replay = 1)
}