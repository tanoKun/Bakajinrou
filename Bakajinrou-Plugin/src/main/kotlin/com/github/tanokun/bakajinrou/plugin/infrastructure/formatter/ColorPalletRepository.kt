package com.github.tanokun.bakajinrou.plugin.infrastructure.formatter

import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import kotlinx.serialization.json.Json
import org.bukkit.plugin.Plugin
import java.io.File

class ColorPalletRepository(plugin: Plugin) {
    private val fileName = "assets/formatter/colorPallet.json"
    private val palletFile = File(plugin.dataFolder, fileName)

    init {
        val folder = File(plugin.dataFolder, "assets/formatter")
        val resource = plugin.getResource(fileName) ?: throw IllegalStateException("リソースファイル: $fileName が見つかりません。")

        if (!folder.exists()) folder.mkdirs()
        if (!palletFile.exists()) {
            resource.use { input ->
                palletFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    fun load(): ColorPallet {
        val text = palletFile.readText()
        return Json.decodeFromString<ColorPallet>(text)
    }
}