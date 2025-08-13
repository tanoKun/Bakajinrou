package com.github.tanokun.bakajinrou.api.translation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class TranslateKeyTest {

    @Test
    @DisplayName("有効なキーでインスタンス化できる")
    fun createValidKey() {
        val key = TranslationKey("example.key")

        assertEquals("example.key", key.key)
    }

    @Test
    @DisplayName("無効なキーで例外が発生する")
    fun createInvalidKeyWithUpperCase() {
        assertThrows<IllegalArgumentException> { TranslationKey("Invalid.Key") }
        assertThrows<IllegalArgumentException> { TranslationKey("invalid.key.1") }
        assertThrows<IllegalArgumentException> { TranslationKey("invalid-key") }
        assertThrows<IllegalArgumentException> { TranslationKey("_invalid.key") }
        assertThrows<IllegalArgumentException> { TranslationKey(".start.with.dot") }
        assertThrows<IllegalArgumentException> { TranslationKey("end.with.dot.") }
        assertThrows<IllegalArgumentException> { TranslationKey("double..dot") }
        assertThrows<IllegalArgumentException> { TranslationKey("") }
    }

    @Test
    @DisplayName("plus演算子でキーを連結できる")
    fun plusOperatorCombinesKeys() {
        val base = TranslationKey("parent")
        val child = TranslationKey("child")
        val combined = base + child

        assertEquals("parent.child", combined.key)
    }
}