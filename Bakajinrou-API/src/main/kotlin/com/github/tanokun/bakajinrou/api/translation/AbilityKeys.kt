package com.github.tanokun.bakajinrou.api.translation

sealed class AbilityKeys(key: String): TranslationKey("ability.$key") {
    class Result private constructor(key: String) : AbilityKeys("result.$key") { companion object {
        val WOLF = Result("wolf")
        val FOX = Result("fox")
        val CITIZENS = Result("citizens")
    }}
}