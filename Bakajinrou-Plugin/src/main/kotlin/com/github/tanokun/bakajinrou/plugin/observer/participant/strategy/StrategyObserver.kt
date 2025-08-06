package com.github.tanokun.bakajinrou.plugin.observer.participant.strategy

import com.github.tanokun.bakajinrou.api.attack.method.SwordItem
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesProvider
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

class StrategyObserver(
    private val grantedStrategiesProvider: GrantedStrategiesProvider,
    private val mainScope: CoroutineScope
) {
    init {
        mainScope.launch {
            grantedStrategiesProvider.observeDifference(mainScope)
                .filterIsInstance<MethodDifference.MethodRemoved>()
                .filter { it.removedMethod is SwordItem }
                .collect(::collect)
        }
    }

    fun collect(methodDifference: MethodDifference) {

    }
}