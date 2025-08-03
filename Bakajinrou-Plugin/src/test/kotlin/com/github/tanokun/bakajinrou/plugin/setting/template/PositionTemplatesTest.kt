package com.github.tanokun.bakajinrou.plugin.setting.template

import kotlin.test.Test

class PositionTemplatesTest {
    @Test
    fun serializeTest() {
            fun inner(x: Int) = x * 2
            val kFunction = ::inner
            println(kFunction.returnType) // ← ここで KotlinReflectionInternalError
    }
}