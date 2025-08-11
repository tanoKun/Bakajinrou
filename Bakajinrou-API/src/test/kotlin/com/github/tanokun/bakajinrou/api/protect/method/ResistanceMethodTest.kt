package com.github.tanokun.bakajinrou.api.protect.method

import com.github.tanokun.bakajinrou.api.attack.method.ArrowMethod
import com.github.tanokun.bakajinrou.api.attack.method.DamagePotionMethod
import com.github.tanokun.bakajinrou.api.attack.method.SwordMethod
import com.github.tanokun.bakajinrou.api.participant.strategy.GrantedReason
import com.github.tanokun.bakajinrou.api.protect.ProtectResult
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class ResistanceMethodTest : ShouldSpec({
    val method = ResistanceMethod(reason = GrantedReason.INITIALIZE)

    val arrowAttack = mockk<ArrowMethod>()
    val potionAttack = mockk<DamagePotionMethod>()
    val swordAttack = mockk<SwordMethod>()

    context("耐性") {
        should("全ての攻撃の攻撃に対して、成功すべき") {
            method.verifyProtect(arrowAttack) shouldBe ProtectResult.PROTECTED
            method.verifyProtect(potionAttack) shouldBe ProtectResult.PROTECTED
            method.verifyProtect(swordAttack) shouldBe ProtectResult.PROTECTED
        }
    }
})