package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.protect.notification

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.ability.knight.GrantProtectResult
import com.github.tanokun.bakajinrou.game.ability.knight.ProtectAbilityExecutor
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 騎士の加護を監視します。騎士への加護完了の通知を行います。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class CompletedProtectionNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
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
        val knight = playerProvider.getAllowNull(result.knightId) ?: return
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        val message = translator.translate(GameKeys.Ability.Using.PROTECT_MESSAGE, knight.locale(), Component.text(targetName))

        knight.playSound(
            Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f)
        )

        knight.sendMessage(message)
    }
}