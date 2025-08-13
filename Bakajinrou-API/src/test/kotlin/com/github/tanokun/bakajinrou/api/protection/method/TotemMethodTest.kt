package com.github.tanokun.bakajinrou.api.protection.method

import com.github.tanokun.bakajinrou.api.attacking.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attacking.method.DamagePotionMethod
import com.github.tanokun.bakajinrou.api.attacking.method.SwordMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protection.ProtectResult
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class TotemMethodTest : ShouldSpec({
    val method = TotemMethod(reason = GrantedReason.INITIALIZED, verificator = mockk())

    val arrowAttack = mockk<ArrowMethod>()
    val potionAttack = mockk<DamagePotionMethod>()
    val swordAttack = mockk<SwordMethod>()

    context("盾") {
        should("全ての攻撃の攻撃に対して、成功すべき") {
            method.verifyProtect(arrowAttack) shouldBe ProtectResult.PROTECTED
            method.verifyProtect(potionAttack) shouldBe ProtectResult.PROTECTED
            method.verifyProtect(swordAttack) shouldBe ProtectResult.PROTECTED
        }
    }
})