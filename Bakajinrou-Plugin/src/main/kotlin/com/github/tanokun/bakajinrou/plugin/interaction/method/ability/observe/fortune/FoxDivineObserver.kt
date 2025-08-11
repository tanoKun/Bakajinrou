package com.github.tanokun.bakajinrou.plugin.interaction.method.ability.observe.fortune

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineResult
import com.github.tanokun.bakajinrou.plugin.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

/**
 * 占いを監視します。占い対象が「妖狐」である場合の処理を行います。
 *
 * - 発光
 * - 「妖狐」への通知
 */
class FoxDivineObserver(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val game: JinrouGame,
    mainScope: CoroutineScope,
    executor: DivineAbilityExecutor,
): Observer {
    init {
        mainScope.launch {
            executor
                .observeDivine(mainScope)
                .filterIsInstance<DivineResult.FoundResult>()
                .mapNotNull { it to (game.getParticipant(it.targetId) ?: return@mapNotNull null) }
                .filter { (_, participant) -> participant.isPosition<FoxPosition>() }
                .collect { (result, _) -> divined(result) }
        }
    }

    private suspend fun divined(result: DivineResult.FoundResult) {
        val fox = playerProvider.waitPlayerOnline(result.fortuneId)
    }
}