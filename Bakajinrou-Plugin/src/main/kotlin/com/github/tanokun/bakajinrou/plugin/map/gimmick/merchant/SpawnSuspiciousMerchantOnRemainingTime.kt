package com.github.tanokun.bakajinrou.plugin.map.gimmick.merchant

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class SpawnSuspiciousMerchantOnRemainingTime(
    private val scheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    private val flow: SuspiciousMerchantFlow,
) {
    fun start() {
        mainScope.launch {
            scheduler.observe(mainScope)
                .filterIsInstance<ScheduleState.Active>()
                .filter { it.remainingTime <= 10.minutes }
                .take(1)
                .collect { flow.spawnMerchant() }
        }
    }
}
