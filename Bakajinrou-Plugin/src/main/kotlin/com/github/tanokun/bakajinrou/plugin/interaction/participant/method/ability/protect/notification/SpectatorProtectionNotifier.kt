package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.protect.notification

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.or
import com.github.tanokun.bakajinrou.api.participant.position.isSpectator
import com.github.tanokun.bakajinrou.game.ability.knight.GrantProtectResult
import com.github.tanokun.bakajinrou.game.ability.knight.ProtectAbilityExecutor
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
 * 加護を行ったことを、観戦者に通知します。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SpectatorProtectionNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val game: JinrouGame,
    mainScope: CoroutineScope,
    executor: ProtectAbilityExecutor,
): Observer {
    init {
        mainScope.launch {
            executor
                .observeProtect(mainScope)
                .filterIsInstance<GrantProtectResult.Granted>()
                .collect(::protected)
        }
    }

    private fun protected(result: GrantProtectResult.Granted) {
        val knightName = PlayerNameCache.get(result.knightId) ?: "unknown"
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        game.getCurrentParticipants()
            .includes(::isSpectator or Participant::isDead)
            .mapNotNull { playerProvider.getAllowNull(it) }
            .forEach {
                val message = translator.translate(
                    DisplayLoggingKeys.Use.PROTECT, it.locale(), Component.text(knightName), Component.text(targetName))

                it.sendMessage(message)
            }
    }
}