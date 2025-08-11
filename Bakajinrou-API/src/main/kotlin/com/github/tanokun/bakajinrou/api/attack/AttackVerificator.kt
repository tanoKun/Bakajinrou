package com.github.tanokun.bakajinrou.api.attack

import com.github.tanokun.bakajinrou.api.attack.method.AttackMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.protect.ProtectResult
import com.github.tanokun.bakajinrou.api.protect.method.ProtectiveMethod


/**
 * 攻撃と防御の相互作用を検証し、戦闘の結果を判定します。
 *
 * このオブジェクトは、特定のエンティティに属さない、純粋なドメインルールをカプセル化します。
 * 渡された引数のみに基づいて計算を行い、戦闘の最終的な結果を返却します。
 *
 * このサービスは、実際の状態は行いません。
 */
object AttackVerificator {

    /**
     * 指定された攻撃手段と被害者を基に、攻撃が成功するか、あるいは防御されるかを判定します。
     *
     * また、防御が成功になるまで防御手段は消費されます。
     *
     * @param attackMethod 実行された攻撃手段
     * @param victim 攻撃の対象となる参加者
     * @return [AttackByMethodResult] 攻撃の判定結果。防御された場合は [AttackByMethodResult.Protected]、成功した場合は [AttackByMethodResult.SucceedAttack]。
     */
    fun attack(attackMethod: AttackMethod, victim: Participant): AttackByMethodResult {
        val consumedProtections = arrayListOf<ProtectiveMethod>()
        for (protectiveMethod in victim.getValidProtectiveMethods()) {
            consumedProtections.add(protectiveMethod)

            when (protectiveMethod.verifyProtect(method = attackMethod)) {
                ProtectResult.PROTECTED -> {
                    return AttackByMethodResult.Protected(consumedProtections)
                }
                ProtectResult.FAILURE -> continue
            }
        }

        return AttackByMethodResult.SucceedAttack(consumedProtections)
    }
}