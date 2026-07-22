package com.github.tanokun.bakajinrou.api.participant.strategy

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.asMethodId
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class GrantedStrategyTest : ShouldSpec({
    fun method(): GrantedMethod = mockk {
        every { methodId } returns UUID.randomUUID().asMethodId()
    }

    context("removeAll") {
        should("指定した複数の手段をすべて剥奪すべき") {
            val a = method()
            val b = method()
            val c = method()

            val strategy = GrantedStrategy(mapOf()).grant(a).grant(b).grant(c)

            val result = strategy.removeAll(listOf(a, b))

            result.getMethod(a.methodId) shouldBe null
            result.getMethod(b.methodId) shouldBe null
            result.getMethod(c.methodId) shouldBe c
        }
    }
})
