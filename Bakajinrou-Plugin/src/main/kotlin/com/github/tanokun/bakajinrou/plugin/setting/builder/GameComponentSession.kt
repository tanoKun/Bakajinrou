package com.github.tanokun.bakajinrou.plugin.setting.builder

import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.setting.builder.observer.BindingObservers

data class GameComponentSession(
    val bindingObservers: BindingObservers,
    val gameController: JinrouGameController,
    ) {
}