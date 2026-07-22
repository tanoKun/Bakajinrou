package com.github.tanokun.bakajinrou.plugin.participant.method.advantage.using.exchange
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.method.advantage.using.ExchangeInfo
import com.github.tanokun.bakajinrou.game.method.advantage.using.LocationExchanger
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 位置交換([com.github.tanokun.bakajinrou.game.method.advantage.using.ExchangeInfo])を監視し、
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
                .observeExchanging()
                .collect(::exchange)
        }
    }

    private fun exchange(info: ExchangeInfo) {
        when (info) {
            is ExchangeInfo.Succeeded -> succeed(info)
            is ExchangeInfo.NoTarget -> fail(info)
        }
    }

    private fun succeed(info: ExchangeInfo.Succeeded) {
        val user = playerProvider.getAllowNull(info.userId) ?: return
        val target = playerProvider.getAllowNull(info.targetId) ?: return

        val userLocation = user.location
        val targetLocation = target.location

        user.teleport(targetLocation)
        target.teleport(userLocation)

        val message = translator.translate(GameKeys.Advantage.Using.EXCHANGE_MESSAGE, user.locale(), Component.text(target.name))

        user.sendMessage(message)
    }

    private fun fail(info: ExchangeInfo.NoTarget) {
        val user = playerProvider.getAllowNull(info.userId) ?: return

        user.playSound(user.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f)
        user.sendMessage(translator.translate(GameKeys.Advantage.Using.FAILED_EXCHANGE_MESSAGE, user.locale()))
    }
}
