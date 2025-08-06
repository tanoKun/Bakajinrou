package com.github.tanokun.bakajinrou.game.attack

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.attack.AttackByMethodResult
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

/**
 * 攻撃手段ごとの攻撃を統括処理するコントローラーです。
 *
 * 実際の攻撃は、各 [com.github.tanokun.bakajinrou.api.method.AttackMethod] に委譲します。
 * 攻撃後には攻撃手段の消費処理を行い、使用済みの手段を攻撃者から剥奪します。
 * また、攻撃結果はすべてデバッグログとして記録されます。
 *
 * このクラスの責務：
 * - 各攻撃手段に攻撃処理を委譲する
 * - 使用済みの手段を消費、剥奪する
 * - 攻撃結果のログを記録する

 * @param debug 攻撃結果のログ記録に使用する
 *
 * @see com.github.tanokun.bakajinrou.api.method.AttackMethod.attack
 * @see com.github.tanokun.bakajinrou.api.attack.method.SwordItem
 * @see com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
 * @see com.github.tanokun.bakajinrou.api.attack.method.DamagePotionEffect
 */
class AttackController(
    private val game: JinrouGame,
    private val debug: DebugLogger,
) {
    private val _attackResolution = MutableSharedFlow<AttackResolution>()

    /**
     * 攻撃手段によって [by] が [victims] へ攻撃します。
     *
     * @param by 攻撃を行う参加者のID
     * @param victims 攻撃される参加者のID
     * @param with 攻撃手段として使用する [AttackMethod]
     *
     * @see AttackMethod.attack
     * @see AttackMethod
     */
    fun attack(by: UUID, victims: List<UUID>, with: AttackMethod) {
        val attackerParticipant = game.getParticipant(by) ?: return
        val victimParticipant = victims.mapNotNull { game.getParticipant(it) }

        val newAttacker = attackerParticipant.removeMethod(with)

        val updates = victimParticipant.map { victim ->
            val result = with.attack(victim).apply {
                debug.logAttacked(by, victim.uniqueId, with, this)
            }

            when (result) {
                is AttackByMethodResult.SucceedAttack -> {
                    _attackResolution.tryEmit(AttackResolution.Killed(attackerParticipant, result))
                    return@map result.deadParticipant
                }
                is AttackByMethodResult.Protected -> {
                    _attackResolution.tryEmit(AttackResolution.Alive(attackerParticipant, result))
                    return@map result.aliveParticipant
                }
            }
        }

        game.updateParticipants(updates + newAttacker)
    }
}