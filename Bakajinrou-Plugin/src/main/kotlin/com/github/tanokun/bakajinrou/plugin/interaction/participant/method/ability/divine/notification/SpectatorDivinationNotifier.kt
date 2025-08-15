package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.divine.notification

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.or
import com.github.tanokun.bakajinrou.api.participant.position.isSpectator
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineResult
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.DisplayLoggingKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 占いの結果を、観戦者に通知します。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SpectatorDivinationNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val game: JinrouGame,
    mainScope: CoroutineScope,
    executor: DivineAbilityExecutor
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
        val fortuneName = PlayerNameCache.get(result.fortuneId) ?: "unknown"
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        game.getCurrentParticipants()
            .includes(::isSpectator or Participant::isDead)
            .mapNotNull { playerProvider.getAllowNull(it) }
            .forEach {
                val resultComponent = translator.translate(result.source.resultKey, it.locale())
                val message = translator.translate(
                    DisplayLoggingKeys.Use.DIVINE, it.locale(), Component.text(fortuneName), Component.text(targetName), resultComponent)

                it.sendMessage(message)
            }
    }
}