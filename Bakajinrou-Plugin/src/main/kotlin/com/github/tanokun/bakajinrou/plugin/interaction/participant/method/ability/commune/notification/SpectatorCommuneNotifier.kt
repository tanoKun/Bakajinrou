package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.commune.notification

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.or
import com.github.tanokun.bakajinrou.api.participant.position.isSpectator
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneResult
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.DisplayLoggingKeys
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 霊媒の結果を、観戦者に通知します。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SpectatorCommuneNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val game: JinrouGame,
    mainScope: CoroutineScope,
    executor: CommuneAbilityExecutor
): Observer {
    init {
        mainScope.launch {
            executor
                .observeCommune(mainScope)
                .filterIsInstance<CommuneResult.Success>()
                .collect(::communed)
        }
    }

    private fun communed(result: CommuneResult.Success) {
        val mediumName = PlayerNameCache.get(result.mediumId) ?: "unknown"
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        val result = when (result) {
            is CommuneResult.FoundResult -> result.source.resultKey
            is CommuneResult.IsNotDead -> GameKeys.Ability.Using.COMMUNE_FAILURE_MESSAGE
        }

        game.getCurrentParticipants()
            .includes(::isSpectator or Participant::isDead)
            .mapNotNull { playerProvider.getAllowNull(it) }
            .forEach {
                val resultComponent = translator.translate(result, it.locale())
                val message = translator.translate(
                    DisplayLoggingKeys.Use.COMMUNE, it.locale(), Component.text(mediumName), Component.text(targetName), resultComponent)

                it.sendMessage(message)
            }

    }
}