package com.github.tanokun.bakajinrou.plugin.setting.builder.observer

import com.github.tanokun.bakajinrou.plugin.observer.participant.strategy.StrategyObserver
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

class ParticipantObservers(override val scope: Scope) : KoinScopeComponent {
    init {
        StrategyObserver(scope.get(), scope.get())
    }
}