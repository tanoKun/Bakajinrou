package com.github.tanokun.bakajinrou.plugin

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.tanokun.bakajinrou.game.map.GameMapRegistry
import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import com.github.tanokun.bakajinrou.plugin.common.setting.template.DistributionTemplates
import com.github.tanokun.bakajinrou.plugin.infrastructure.GameMapRepositoryImpl
import com.github.tanokun.bakajinrou.plugin.infrastructure.formatter.ColorPalletRepository
import com.github.tanokun.bakajinrou.plugin.infrastructure.localization.DictionaryRepository
import com.github.tanokun.bakajinrou.plugin.infrastructure.template.PositionTemplateRepository
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import java.io.File

class AsyncSettingLoader(private val plugin: Plugin) {
    private var colorPallet: ColorPallet? = null
    private var translator: JinrouTranslator? = null
    private var gameMapRegistry: GameMapRegistry? = null
    private var distributionTemplates: DistributionTemplates? = null

    private val logger = plugin.logger
    private val minecraftDispatcher = plugin.minecraftDispatcher

    private val colorPalletMutex: Mutex = Mutex()
    private val translatorMutex: Mutex = Mutex()
    private val gameMapRegistryMutex: Mutex = Mutex()
    private val distributionTemplatesMutex: Mutex = Mutex()

    suspend fun loadColorPallet(): ColorPallet = colorPalletMutex.withLock {
        colorPallet?.let { return it }

        logger.info("カラーパレットを読み込み中...")
        val colorPalletRepository = ColorPalletRepository(plugin)
        val colorPallet = colorPalletRepository.load()
        this.colorPallet = colorPallet

        withContext(minecraftDispatcher) {
            logger.info("カラーパレットの読み込みが完了しました。")
        }

        return colorPallet
    }

    suspend fun loadTranslator(): JinrouTranslator = translatorMutex.withLock {
        translator?.let { return it }

        val colorPallet = loadColorPallet()
        val dictionaryRepository = DictionaryRepository(plugin)

        logger.info("言語設定を読み込み中...")
        val dictionaries = dictionaryRepository.loadAll()
        val translator = JinrouTranslator(dictionaries, colorPallet)
        this.translator = translator

        withContext(minecraftDispatcher) {
            logger.info("言語設定の読み込みが完了しました。")
        }

        return translator
    }

    suspend fun loadMaps(): GameMapRegistry = gameMapRegistryMutex.withLock {
        gameMapRegistry?.let { return it }

        logger.info("人狼マップを読み込み中...")
        val gameMapRepository = GameMapRepositoryImpl(File(plugin.dataFolder, "maps"))
        val gameMapRegistry = GameMapRegistry(gameMapRepository).apply { loadAllMapsFromRepository() }
        this.gameMapRegistry = gameMapRegistry

        withContext(minecraftDispatcher) {
            logger.info("人狼マップの読み込みが完了しました。")
        }

        return gameMapRegistry
    }

    suspend fun loadTemplate(): DistributionTemplates = distributionTemplatesMutex.withLock {
        distributionTemplates?.let { return it }

        logger.info("役職テンプレートを読み込み中...")
        val templateRepository = PositionTemplateRepository(plugin)
        val templates = templateRepository.load()
        this.distributionTemplates = templates

        withContext(minecraftDispatcher) {
            logger.info("役職テンプレートの読み込みが完了しました。")
        }

        return templates
    }
}