package com.github.tanokun.bakajinrou.plugin.setting.builder.game

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test

class DIContextTest {

    class A
    class B(val a: A)
    class C(val a: A, val b: B)

    @Test
    @DisplayName("registerでインスタンスを登録できる")
    fun registerInstance() {
        val context = DIContext()
        val instance = A()

        context.register(instance)

        val resolved = context.get<A>()
        assertSame(instance, resolved)
    }

    @Test
    @DisplayName("getで型からインスタンスを取得できる")
    fun getInstanceByClass() {
        val context = DIContext()
        val instance = A()

        context.register(A::class, instance)

        val resolved = context.get(A::class)
        assertSame(instance, resolved)
    }

    @Test
    @DisplayName("constructで依存関係付きのインスタンスを生成できる")
    fun constructInstanceWithDependencies() {
        val context = DIContext()
        val a = A()
        val b = B(a)
        context.register(a)
        context.register(b)

        val constructed = context.construct(C::class)

        assertSame(a, constructed.a)
        assertSame(b, constructed.b)
    }

    @Test
    @DisplayName("getで未登録の型を取得すると例外が発生する")
    fun getThrowsIfNotRegistered() {
        val context = DIContext()

        val exception = assertThrows(IllegalStateException::class.java) {
            context.get<A>()
        }

        assertTrue(exception.message!!.contains("対応していない型です"))
    }

    @Test
    @DisplayName("getParametersで関数に必要な引数を解決できる")
    fun resolveFunctionParameters() {
        val context = DIContext()
        val a = A()
        val b = B(a)
        context.register(a)
        context.register(b)
        val resolved = context.getParameters(::fn)

        assertEquals(listOf(a, b), resolved.toList())
    }

    fun fn(a: A, b: B): String { return "ok" }
}