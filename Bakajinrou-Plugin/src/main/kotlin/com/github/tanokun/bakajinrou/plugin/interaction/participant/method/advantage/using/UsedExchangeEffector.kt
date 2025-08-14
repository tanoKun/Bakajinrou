package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.advantage.using

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.method.advantage.using.ExchangeInfo
import com.github.tanokun.bakajinrou.game.method.advantage.using.LocationExchanger
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 位置交換イベント([ExchangeInfo])を監視し、
 * 実際にプレイヤーをテレポートさせ、メッセージを送信します。
 *
 * このクラスは、位置交換ユースケースの「結果」に対するリアクションを担当します。
 * 交換のトリガーやビジネスロジックには関与しません。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class UsedExchangeEffector(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
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
        val user = playerProvider.getAllowNull(info.userId) ?: return
        val target = playerProvider.getAllowNull(info.targetId) ?: return

        val userLocation = user.location
        val targetLocation = target.location

        user.teleport(targetLocation)
        target.teleport(userLocation)

        val message = translator.translate(GameKeys.Advantage.Using.EXCHANGE_MESSAGE, user.locale(), Component.text(target.name))

        user.sendMessage(message)
    }
}