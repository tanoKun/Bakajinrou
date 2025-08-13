package com.github.tanokun.bakajinrou.plugin.interaction.method.ability.use.medium

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.medium.CommuneResult
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 霊媒を監視します。霊媒結果の表示を主な責務としています。
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class UsedCommuneObserver(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
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
        val medium = playerProvider.getAllowNull(result.mediumId) ?: return
        val targetName = PlayerNameCache.get(result.targetId) ?: "unknown"

        if (result is CommuneResult.FoundResult) {
            val resultComponent = translator.translate(result.source.resultKey, medium.locale())
            val message = translator.translate(GameKeys.Ability.Using.COMMUNE_MESSAGE, medium.locale(), Component.text(targetName), resultComponent)

            medium.sendMessage(message)
        }

        if (result is CommuneResult.IsNotDead) {
            val message = translator.translate(GameKeys.Ability.Using.COMMUNE_FAILURE_MESSAGE, medium.locale(), Component.text(targetName))

            medium.sendMessage(message)
        }

    }
}