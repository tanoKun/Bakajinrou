package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.advantage.using.exchange

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.or
import com.github.tanokun.bakajinrou.api.participant.position.isSpectator
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.game.method.advantage.using.ExchangeInfo
import com.github.tanokun.bakajinrou.game.method.advantage.using.LocationExchanger
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.DisplayLoggingKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 「位置交換」したことを観戦者に通知します。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class SpectatorUsedExchangeNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val game: JinrouGame,
    mainScope: CoroutineScope,
    locationExchanger: LocationExchanger,
): Observer {
    init {
        mainScope.launch {
            locationExchanger
                .observeExchanging(mainScope)
                .collect(::exchanged)
        }
    }

    private fun exchanged(info: ExchangeInfo) {
        val userName = PlayerNameCache.get(info.userId) ?: return
        val targetName = PlayerNameCache.get(info.targetId) ?: return

        game.getCurrentParticipants()
            .includes(::isSpectator or Participant::isDead)
            .mapNotNull { playerProvider.getAllowNull(it) }
            .forEach {
                val message = translator.translate(
                    DisplayLoggingKeys.Use.EXCHANGE_METHOD, it.locale(), Component.text(userName), Component.text(targetName))

                it.sendMessage(message)
            }
    }
}