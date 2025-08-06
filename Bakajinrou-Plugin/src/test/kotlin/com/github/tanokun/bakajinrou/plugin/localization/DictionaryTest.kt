package com.github.tanokun.bakajinrou.plugin.localization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class DictionaryTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun test() {

        val json = Json {
            prettyPrintIndent = "    "
            prettyPrint = true
        }

        val test = Dictionary(mapOf(
            UITranslationKeys.Formatter.Category.WOLF.key to "<wolf><bold>《 人狼 》</bold></wolf>"
        ))

        println(json.encodeToString(test))
        println(0xFFAA00)
    }
}