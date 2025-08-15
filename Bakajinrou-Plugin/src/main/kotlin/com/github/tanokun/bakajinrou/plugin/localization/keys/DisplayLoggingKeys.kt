package com.github.tanokun.bakajinrou.plugin.localization.keys

import com.github.tanokun.bakajinrou.api.translation.TranslationKey

open class DisplayLoggingKeys private constructor(key: String): TranslationKey("logging.$key") {
    companion object {
        val KILL = DisplayLoggingKeys("kill")
    }

    class Use private constructor(key: String): DisplayLoggingKeys("use.$key") { companion object {
        val DIVINE = Use("divine")
        val COMMUNE = Use("commune")
        val PROTECT = Use("protect")

        val EXCHANGE_METHOD = Use("exchange.method")
    } }
}