package com.github.tanokun.bakajinrou.plugin.map.gimmick.hijack

import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.whenLaunched
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmick
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickContext
import com.github.tanokun.bakajinrou.plugin.map.gimmick.MapGimmickId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.milliseconds

@Scoped(binds = [MapGimmick::class])
@Scope(value = GameComponents::class)
class HijackGimmick(
    plugin: Plugin,
    context: MapGimmickContext,
    private val scheduler: GameScheduler,
    private val mainScope: CoroutineScope,
    playerProvider: BukkitPlayerProvider,
) : MapGimmick {
    override val id = MapGimmickId.HIJACK

    private val items = HijackItems(NamespacedKey(plugin, "hijack_floor"))
    private val initializer = HijackItemInitializer(context, playerProvider, items)
    private val flow = HijackFlow(context, playerProvider)
    private val trigger = TriggerHijackByInventoryClick(plugin, context, items, flow)
    private val invalidOperationCanceller = CancelInvalidHijackOperationListener(plugin, items)

    override fun start() {
        invalidOperationCanceller.registerAll()
        trigger.registerAll()

        mainScope.launch {
            scheduler.observe(mainScope)
                .whenLaunched()
                .take(1)
                .collect {
                    delay(50.milliseconds)
                    initializer.initialize()
                }
        }
    }

    override fun close() {
        trigger.unregisterAll()
        invalidOperationCanceller.unregisterAll()
        initializer.clear()
    }
}
