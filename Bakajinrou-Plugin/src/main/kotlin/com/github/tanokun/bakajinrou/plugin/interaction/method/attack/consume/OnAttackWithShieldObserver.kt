package com.github.tanokun.bakajinrou.plugin.interaction.method.attack.consume

import com.github.tanokun.bakajinrou.api.observer.Observer
import com.github.tanokun.bakajinrou.api.protect.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.protect.method.ShieldMethod
import com.github.tanokun.bakajinrou.game.attack.AttackResolution
import com.github.tanokun.bakajinrou.game.attack.Attacking
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory

/**
 * 防御手段「盾」が、攻撃に対して使用された際の効果を担当します。
 *
 * @property attacking 攻撃イベントの公開者
 * @property mainScope 監視用のコルーチンを起動するためのスコープ
 * @property playerProvider BukkitのPlayerオブジェクトを取得するための Provider
 */
class OnAttackWithShieldObserver(
    attacking: Attacking,
    private val mainScope: CoroutineScope,
    private val playerProvider: BukkitPlayerProvider,
): Observer {
    init {
        mainScope.launch {
            attacking.observeAttack(mainScope)
                .filter { it.result.consumedProtectiveMethods.any(::isShield) }
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

        victim.world.playSound(victim.location, Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0f, 1.0f)
        victim.world.spawnParticle(Particle.BLOCK, victim.location, 10, 0.3, 0.3, 0.3, Material.OAK_WOOD.createBlockData())
    }

    fun isShield(method: ProtectiveMethod) = method is ShieldMethod
}