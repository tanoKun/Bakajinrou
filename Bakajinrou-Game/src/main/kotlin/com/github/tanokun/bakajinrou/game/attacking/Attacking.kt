package com.github.tanokun.bakajinrou.game.attacking

import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.api.attacking.AttackByMethodResult
import com.github.tanokun.bakajinrou.api.attacking.AttackVerificator
import com.github.tanokun.bakajinrou.api.attacking.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attacking.method.AttackMethod
import com.github.tanokun.bakajinrou.api.method.MethodId
import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.game.state.GameTransition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds

/**
 * 攻撃手段ごとの攻撃を統括処理するコントローラーです。
 *
 * 実際の攻撃は、各 [AttackMethod] に委譲します。
 * 攻撃後には攻撃手段の消費処理を行い、使用済みの手段を攻撃者から剥奪します。
 *
 * @see com.github.tanokun.bakajinrou.api.attacking.method.SwordMethod
 * @see com.github.tanokun.bakajinrou.api.attacking.method.ArrowMethod
 * @see com.github.tanokun.bakajinrou.api.attacking.method.GasMethod
 */
class Attacking(private val game: GameStore) {
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
    suspend inline fun <reified T: AttackMethod> attack(by: ParticipantId, victims: List<ParticipantId>, withId: MethodId) {
        attack(by, victims, withId, T::class)
    }

    suspend fun <T: AttackMethod> attack(by: ParticipantId, victims: List<ParticipantId>, withId: MethodId, klass: KClass<T>) {
        val attackResolutions = game.transact { currentGame ->
            val attacker = currentGame.getParticipant(by)
                ?: return@transact GameTransition(currentGame, emptyList())
            val attackMethod = attacker.getGrantedMethod(withId) as? AttackMethod
                ?: return@transact GameTransition(currentGame, emptyList())

            if (attackMethod::class != klass) {
                return@transact GameTransition(currentGame, emptyList())
            }

            val resolutions = victims.mapNotNull { victimId ->
                val victim = currentGame.getParticipant(victimId) ?: return@mapNotNull null
                when (val result = AttackVerificator.attack(attackMethod, victim)) {
                    is AttackByMethodResult.SucceedAttack -> AttackResolution.Killed(by, victimId, result)
                    is AttackByMethodResult.Protected -> AttackResolution.Alive(by, victimId, result)
                }
            }

            val afterVictims = resolutions.fold(currentGame) { updating, resolution ->
                updating.updateParticipant(resolution.victimId) { victim ->
                    val consumed = victim.removeAll(resolution.result.consumedProtectiveMethods)
                    if (resolution is AttackResolution.Killed) consumed.dead() else consumed
                }
            }

            val updated = if (resolutions.isEmpty()) afterVictims else {
                afterVictims.updateParticipant(by) { it.removeMethod(attackMethod) }
            }

            GameTransition(updated, resolutions)
        }

        attackResolutions.forEach { _attackResolution.emit(it) }
    }

    /**
     * 矢を発射時、3秒後に新しい矢(攻撃手段) を追加します。
     * 参加者が存在しない場合補充はされません。
     *
     * @param shooter 矢を発射した参加者の Id
     */
    suspend fun shootArrow(shooter: ParticipantId) {
        delay(3.seconds)

        game.transact { currentGame ->
            if (!currentGame.existParticipant(shooter)) {
                return@transact GameTransition(currentGame, Unit)
            }

            GameTransition(
                currentGame.updateParticipant(shooter) { current ->
                    current.grantMethod(ArrowMethod(reason = GrantedReason.SYSTEM))
                },
                Unit,
            )
        }
    }

    /**
     * 特定の矢の手段を [shooter] から剥奪します。
     * 参加者または手段が存在しない場合、何も行いません。
     *
     * @param shooter 矢を発射した参加者の Id
     * @param arrowId 剥奪する矢の手段の Id
     */
    suspend fun consumeArrow(shooter: ParticipantId, arrowId: MethodId) {
        game.transact { currentGame ->
            val target = currentGame.getParticipant(shooter)
                ?: return@transact GameTransition(currentGame, Unit)
            val arrowMethod = target.getGrantedMethod(arrowId) as? ArrowMethod
                ?: return@transact GameTransition(currentGame, Unit)

            GameTransition(
                currentGame.updateParticipant(shooter) { it.removeMethod(arrowMethod) },
                Unit,
            )
        }
    }

    fun observeAttack(): Flow<AttackResolution> = _attackResolution.asSharedFlow()

}
