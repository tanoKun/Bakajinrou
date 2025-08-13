package com.github.tanokun.bakajinrou.api.protection.method

import com.github.tanokun.bakajinrou.api.attacking.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attacking.method.DamagePotionMethod
import com.github.tanokun.bakajinrou.api.attacking.method.SwordMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protection.ProtectResult
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class ShieldMethodTest : ShouldSpec({
    val method = ShieldMethod(reason = GrantedReason.INITIALIZED, verificator = mockk())

    val arrowAttack = mockk<ArrowMethod>()
    val potionAttack = mockk<DamagePotionMethod>()
    val swordAttack = mockk<SwordMethod>()

    context("盾") {
        should("弓のみ防ぐべき") {
            method.verifyProtect(arrowAttack) shouldBe ProtectResult.PROTECTED
            method.verifyProtect(potionAttack) shouldBe ProtectResult.FAILURE
            method.verifyProtect(swordAttack) shouldBe ProtectResult.FAILURE
        }
    }
})