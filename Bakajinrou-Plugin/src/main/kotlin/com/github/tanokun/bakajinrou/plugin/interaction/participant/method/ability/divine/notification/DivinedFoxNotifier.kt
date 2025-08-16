package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.ability.divine.notification

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineAbilityExecutor
import com.github.tanokun.bakajinrou.game.ability.fortune.DivineResult
import com.github.tanokun.bakajinrou.plugin.common.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.NamespacedKey
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import kotlin.time.Duration.Companion.seconds

/**
 * 占いを監視します。占い対象が「妖狐」である場合の処理を行います。
 *
 * - 発光
 * - 「妖狐」への通知
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class DivinedFoxNotifier(
    private val playerProvider: BukkitPlayerProvider,
    private val translator: JinrouTranslator,
    private val game: JinrouGame,
    mainScope: CoroutineScope,
    executor: DivineAbilityExecutor,
): Observer {
    private val glowingTime = 60.seconds

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
        val fox = playerProvider.waitPlayerOnline(result.targetId)

        val message = translator.translate(GameKeys.Ability.Using.DIVINED_FOX_MESSAGE, fox.locale())

        fox.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, glowingTime.toTick(), 1, true, false))
        fox.showTitle(Title.title(Component.text(""), message))
        fox.sendMessage(message)
        fox.playSound(Sound.sound(NamespacedKey("minecraft", "entity.allay.item_taken"), Sound.Source.PLAYER, 10f, 0.5f))
    }
}