package com.github.tanokun.bakajinrou.plugin.method.optional

import com.github.tanokun.bakajinrou.api.method.GrantedMethod

sealed interface OptionalMethod: GrantedMethod {
    interface ClickMethod: OptionalMethod
    interface DrinkingMethod: OptionalMethod
    interface ThrowMethod: OptionalMethod
}