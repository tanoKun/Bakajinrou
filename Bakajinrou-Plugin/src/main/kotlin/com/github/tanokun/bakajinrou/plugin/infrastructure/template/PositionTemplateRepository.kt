package com.github.tanokun.bakajinrou.plugin.infrastructure.template

import com.github.tanokun.bakajinrou.plugin.setting.template.DistributionTemplates
import kotlinx.serialization.json.Json
import org.bukkit.plugin.Plugin
import java.io.File

class PositionTemplateRepository(plugin: Plugin) {
    private val templatesFile = File(plugin.dataFolder, "templates.json")

    init {
        val folder = plugin.dataFolder
        val resource = plugin.getResource("templates.json") ?: throw IllegalStateException("リソースファイル: templates.json が見つかりません。")

        if (!folder.exists()) folder.mkdirs()
        if (!templatesFile.exists()) {
            resource.use { input ->
                templatesFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    fun load(): DistributionTemplates {
        val text = templatesFile.readText()
        return Json.decodeFromString<DistributionTemplates>(text)
    }
}