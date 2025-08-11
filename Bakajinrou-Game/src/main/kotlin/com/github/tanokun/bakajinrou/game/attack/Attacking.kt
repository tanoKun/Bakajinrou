package com.github.tanokun.bakajinrou.game.attack

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.AttackByMethodResult
import com.github.tanokun.bakajinrou.api.attack.AttackVerificator
import com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attack.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.reflect.KClass

/**
 * 攻撃手段ごとの攻撃を統括処理するコントローラーです。
 *
 * 実際の攻撃は、各 [AttackMethod] に委譲します。
 * 攻撃後には攻撃手段の消費処理を行い、使用済みの手段を攻撃者から剥奪します。
 *
 * このクラスの責務：
 * - 各攻撃手段に攻撃処理を委譲する
 * - 使用済みの手段を消費、剥奪する

 *
 * @see AttackMethod.attack
 * @see com.github.tanokun.bakajinrou.api.attack.method.SwordMethod
 * @see com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
 * @see com.github.tanokun.bakajinrou.api.attack.method.DamagePotionMethod
 */
class Attacking(private val game: JinrouGame) {
    private val _attackResolution = MutableSharedFlow<AttackResolution>()

    /**
     * 攻撃手段によって [by] が [victims] へ攻撃します。
     *
     * @param by 攻撃を行う参加者の Id
     * @param victims 攻撃される参加者の Id
     * @param with 攻撃として使用する手段の Id
     * @param T 前提条件となる攻撃手段
     *
     * @see AttackResolution
     * @see AttackVerificator
     * @see AttackMethod
     */
    suspend inline fun <reified T: AttackMethod> attack(by: ParticipantId, victims: List<ParticipantId>, with: MethodId) {
        attack(by, victims, with, T::class)
    }

    suspend fun <T: AttackMethod> attack(by: ParticipantId, victims: List<ParticipantId>, with: MethodId, klass: KClass<T>) {
        val attackerParticipant = game.getParticipant(by) ?: return

        val with = attackerParticipant.getGrantedMethod(with) as? AttackMethod ?: return

        if (with::class != klass) return

        val attackResolutions = victims.mapNotNull { victim ->
            val result = AttackVerificator.attack(with, game.getParticipant(victim) ?: return@mapNotNull null)

            when (result) {
                is AttackByMethodResult.SucceedAttack -> AttackResolution.Killed(attackerId = by, victim, result)
                is AttackByMethodResult.Protected -> AttackResolution.Alive(attackerId = by, victim, result)
            }
        }

        attackResolutions.forEach {
            game.updateParticipant(it.victimId) { victim ->
                val victimAfterConsumption = it.result.consumeProtectiveMethods.fold(victim) { acc, method ->
                    acc.removeMethod(method)
                }

                if (it is AttackResolution.Killed) victimAfterConsumption.dead() else victimAfterConsumption

            }

            game.updateParticipant(it.attackerId) { attacker ->
                attacker.removeMethod(with)
            }
        }

        attackResolutions.forEach { _attackResolution.emit(it) }
    }

    /**
     * 矢を発射時、3秒後に新しい矢(攻撃手段) を追加します。
     * 参加者が存在しない場合補充はされません。
     *
     * @param shooter 矢を発射した参加者の Id
     */
    suspend fun arrowShoot(shooter: ParticipantId) {
        if (!game.existParticipant(shooter)) return

        delay(3000)
        game.updateParticipant(shooter) { current ->
            current.grantMethod(ArrowMethod(reason = GrantedReason.SYSTEM))
        }
    }
}