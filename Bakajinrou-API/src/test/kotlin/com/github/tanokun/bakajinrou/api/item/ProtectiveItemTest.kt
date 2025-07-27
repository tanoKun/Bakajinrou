package com.github.tanokun.bakajinrou.api.item

import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.api.method.AttackMethod
import com.github.tanokun.bakajinrou.api.protect.method.item.FakeTotemItem
import com.github.tanokun.bakajinrou.api.protect.method.item.ShieldItem
import com.github.tanokun.bakajinrou.api.protect.method.item.TotemItem
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProtectiveItemTest {
    @Test
    @DisplayName("フェイクトーテムは全ての攻撃が有効")
    fun fakeTotemSuccessAttackByAllWeaponTest() {
        val fakeTotemItem = spyk<FakeTotemItem>()

        val result = fakeTotemItem.verifyProtect(method = mockk<AttackMethod>())

        assertEquals(AttackResult.SuccessAttack, result)
    }

    @Test
    @DisplayName("トーテムは全ての攻撃を防ぐ")
    fun totemProtectAttackByAllWeaponTest() {
        val totemItem = spyk<TotemItem>()

        val result = totemItem.verifyProtect(method = mockk<AttackMethod>())

        assertEquals(AttackResult.Protected(totemItem), result)
    }

    @Test
    @DisplayName("盾は弓以外の攻撃を防げない")
    fun shieldCannotProtectAttackOtherThanArrows() {
        val shieldItem = spyk<ShieldItem>()

        val result = shieldItem.verifyProtect(method = mockk<AttackMethod>())
        val result2 = shieldItem.verifyProtect(method = mockk<ArrowMethod>())

        assertEquals(AttackResult.SuccessAttack, result)
        assertEquals(AttackResult.Protected(shieldItem), result2)
    }
}