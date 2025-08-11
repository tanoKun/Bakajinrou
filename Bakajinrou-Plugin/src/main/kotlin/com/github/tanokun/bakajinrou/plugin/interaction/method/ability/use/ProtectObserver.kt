package com.github.tanokun.bakajinrou.plugin.interaction.method.ability.use

import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.game.ability.knight.GrantProtectResult
import com.github.tanokun.bakajinrou.game.ability.knight.ProtectAbilityExecutor
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component

/**
 * 騎士の加護を監視します。騎士への加護完了の通知を行います。
 */
class ProtectObserver(
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
        val fortune = playerProvider.getAllowNull(result.knightId) ?: return
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        val message = translator.translate(GameKeys.Ability.Using.PROTECT_MESSAGE, fortune.locale(), Component.text(targetName))

        fortune.sendMessage(message)
    }
}