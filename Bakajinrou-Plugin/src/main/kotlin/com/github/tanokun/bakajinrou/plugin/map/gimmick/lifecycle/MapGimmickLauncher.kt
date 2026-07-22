package com.github.tanokun.bakajinrou.plugin.map.gimmick.lifecycle

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.koin.core.component.KoinScopeComponent
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class MapGimmickLauncher(
    override val scope: org.koin.core.scope.Scope,
    gameMap: GameMap,
    mainScope: CoroutineScope,
) : Observer, KoinScopeComponent {
    private val active = MapGimmickRegistry(scope.getAll()).findBy(gameMap.gimmickId)

    init {
        active?.start()
        mainScope.coroutineContext[Job]?.invokeOnCompletion { active?.close() }
    }
}
