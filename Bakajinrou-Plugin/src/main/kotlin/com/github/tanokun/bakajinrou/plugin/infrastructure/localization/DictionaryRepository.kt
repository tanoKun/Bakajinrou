package com.github.tanokun.bakajinrou.plugin.infrastructure.localization

import com.github.tanokun.bakajinrou.plugin.localization.Dictionary
import kotlinx.serialization.json.Json
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.*

class DictionaryRepository(plugin: Plugin) {
    val folder = File(plugin.dataFolder, "lang")

    init {
        if (!folder.exists()) folder.mkdirs()

        val jaJp = "lang/ja_jp.json"
        val resource = plugin.getResource(jaJp) ?: throw IllegalStateException("リソースファイル: $jaJp が見つかりません。")

        val jaJpFile = File(plugin.dataFolder, jaJp)

        if (!jaJpFile.exists()) {
            resource.use { input ->
                jaJpFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    fun loadAll(): Map<Locale, Dictionary> = folder
        .listFiles { it.extension == "json" }
        .associate {
            val localeName = it.nameWithoutExtension.split("_")
            Locale.of(localeName[0], localeName[1]) to loadByFile(it)
        }

    private fun loadByFile(file: File): Dictionary {
        val text = file.readText()
        val dictionary = Json.decodeFromString<Dictionary>(text)

        return dictionary
    }
}