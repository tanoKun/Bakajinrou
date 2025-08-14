package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.resistance

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.protection.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.protection.method.ResistanceMethod
import com.github.tanokun.bakajinrou.game.attacking.AttackResolution
import com.github.tanokun.bakajinrou.game.attacking.Attacking
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.potion.PotionEffectType
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 防御手段「耐性」が、攻撃に対して使用された際の効果を担当します。
 *
 * @property attacking 攻撃イベントの公開者
 * @property mainScope 監視用のコルーチンを起動するためのスコープ
 * @property playerProvider BukkitのPlayerオブジェクトを取得するための Provider
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class ConsumedResistanceByAttackEffector(
    attacking: Attacking,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
): Observer {
    init {
        mainScope.launch {
            attacking.observeAttack(mainScope)
                .filter { it.result.consumedProtectiveMethods.any(::isResistanceMethod) }
                .collect(::usedResistance)
        }
    }

    /**
     * 防御手段「耐性」が、攻撃に対して使用された際に呼び出されます。
     *
     * @param resolution 攻撃の結果情報。
     */
    fun usedResistance(resolution: AttackResolution) {
        val victim = playerProvider.getAllowNull(resolution.victimId) ?: return

        victim.removePotionEffect(PotionEffectType.RESISTANCE)
        victim.world.playSound(victim.location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 1.0f, 1.0f)
    }

    fun isResistanceMethod(method: ProtectiveMethod) = method is ResistanceMethod
}