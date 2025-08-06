package com.github.tanokun.bakajinrou.plugin.setting.builder.observer

import com.github.tanokun.bakajinrou.plugin.observer.scheduler.TimeAnnouncer
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

class ScheduleObservers(override val scope: Scope) : KoinScopeComponent {
    init {
        TimeAnnouncer(scope.get(), scope.get(), scope.get())
    }
}