package com.github.tanokun.bakajinrou.plugin.setting.builder.observer

import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

class BindingObservers(override val scope: Scope) : KoinScopeComponent {
    init {
        InitializationObservers(scope)
        ScheduleObservers(scope)
        ParticipantObservers(scope)
    }
}