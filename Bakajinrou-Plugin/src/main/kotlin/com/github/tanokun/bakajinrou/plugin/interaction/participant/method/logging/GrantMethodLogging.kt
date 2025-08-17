package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.logging

import com.github.ajalt.mordant.rendering.TextColors.Companion.rgb
import com.github.ajalt.mordant.terminal.Terminal
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedStrategiesPublisher
import com.github.tanokun.bakajinrou.api.participant.strategy.MethodDifference
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class GrantMethodLogging(
    private val grantedStrategiesPublisher: GrantedStrategiesPublisher,
    private val mainScope: CoroutineScope,
    private val terminal: Terminal,
): Observer {
    init {
        mainScope.launch {
            grantedStrategiesPublisher.observeDifference()
                .filterIsInstance<MethodDifference.Granted>()
                .collect(::logging)
        }
    }

    @OptIn(FlowPreview::class)
    private fun logging(granted: MethodDifference.Granted) = mainScope.launch {
        val header = rgb("#E0E0E0")("Debug - Granted Method")
        terminal.println(header)

        val details = listOf(
            "Target -> ${PlayerNameCache.get(granted.participantId)} (${granted.participantId.uniqueId})",
            "Method -> ${granted.grantedMethod}",
            "Reason -> ${granted.grantedMethod.reason}",
        )

        details.forEach {
            terminal.println("  " + rgb("#909090")("- $it"))
        }
    }
}