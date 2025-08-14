package com.github.tanokun.bakajinrou.plugin.interaction.participant.method.protection.totem

import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.api.protection.method.FakeTotemMethod
import com.github.tanokun.bakajinrou.api.protection.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.protection.method.TotemMethod
import com.github.tanokun.bakajinrou.game.attacking.AttackResolution
import com.github.tanokun.bakajinrou.game.attacking.Attacking
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

/**
 * 防御手段「トーテム」が、攻撃に対して使用された際の効果を担当します。
 *
 * @property attacking 攻撃イベントの公開者
 * @property mainScope 監視用のコルーチンを起動するためのスコープ
 * @property playerProvider BukkitのPlayerオブジェクトを取得するための Provider
 */
@Scoped(binds = [Observer::class])
@Scope(value = GameComponents::class)
class ConsumedTotemByAttackEffector(
    attacking: Attacking,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
): Observer {
    init {
        mainScope.launch {
            attacking.observeAttack(mainScope)
                .filter { it.result.consumedProtectiveMethods.any(::isTotem) }
                .collect(::usedTotem)
        }
    }

    /**
     * 防御手段「耐性」が、攻撃に対して使用された際に呼び出されます。
     *
     * @param resolution 攻撃の結果情報。
     */
    fun usedTotem(resolution: AttackResolution) {
        val victim = playerProvider.getAllowNull(resolution.victimId) ?: return

        victim.world.playSound(victim.location, Sound.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f)
        victim.world.spawnParticle(Particle.TOTEM_OF_UNDYING, victim.location, 30, 0.5, 0.5, 0.5, 0.1)
    }

    fun isTotem(method: ProtectiveMethod) = method is TotemMethod || method is FakeTotemMethod
}