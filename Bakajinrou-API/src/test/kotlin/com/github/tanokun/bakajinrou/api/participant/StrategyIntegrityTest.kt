package com.github.tanokun.bakajinrou.api.participant

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class StrategyIntegrityTest {
    private val strategyIntegrity = StrategyIntegrity()

    @Test
    @DisplayName("重複した手段は有効にできない")
    fun cannotGrantDuplicationMethodTest() {
        val method = mockk<GrantedMethod>().apply {
            every { uniqueId } returns UUID.randomUUID()
        }

        strategyIntegrity.enableMethod(method)

        assertThrows<IllegalArgumentException>("重複した手段です。") {
            strategyIntegrity.enableMethod(method)
        }
    }

    @Test
    @DisplayName("有効ではない手段は無効にできない。")
    fun cannotDisableNotActiveMethodTest() {
        val method = mockk<GrantedMethod>().apply {
            every { uniqueId } returns UUID.randomUUID()
        }
        assertThrows<IllegalArgumentException>("登録されていない手段です。") {
            strategyIntegrity.disableMethod(method)
        }
    }

    @Test
    @DisplayName("適切な手段は有効にできる。")
    fun canEnableTest() {
        val method = mockk<GrantedMethod>().apply {
            every { uniqueId } returns UUID.randomUUID()
        }

        strategyIntegrity.enableMethod(method)
        strategyIntegrity.disableMethod(method)
        strategyIntegrity.enableMethod(method)
    }
}