package com.github.tanokun.bakajinrou.plugin.interaction.method.ability.observe.fortune

import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineResult
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component

/**
 * 占いを監視します。占い結果の表示を主な責務としています。
 */
class DivineObserver(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    mainScope: CoroutineScope,
    executor: DivineAbilityExecutor,
): Observer {
    init {
        mainScope.launch {
            executor
                .observeDivine(mainScope)
                .filterIsInstance<DivineResult.FoundResult>()
                .collect(::divined)
        }
    }

    private fun divined(result: DivineResult.FoundResult) {
        val fortune = playerProvider.getAllowNull(result.fortuneId) ?: return
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        val resultComponent = translator.translate(result.source.resultKey, fortune.locale())
        val message = translator.translate(GameKeys.Ability.Using.DIVINE_MESSAGE, fortune.locale(), Component.text(targetName), resultComponent)

        fortune.sendMessage(message)
    }
}