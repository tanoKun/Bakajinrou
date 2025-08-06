package com.github.tanokun.bakajinrou.plugin.setting.builder.observer

import com.github.tanokun.bakajinrou.game.observer.initializer.InherentMethodsInitializer
import com.github.tanokun.bakajinrou.plugin.observer.init.WolfInitializer
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

class InitializationObservers(override val scope: Scope) : KoinScopeComponent {
    init {
        InherentMethodsInitializer(scope.get(), scope.get())
        WolfInitializer(scope.get(), scope.get(), scope.get())
    }
}