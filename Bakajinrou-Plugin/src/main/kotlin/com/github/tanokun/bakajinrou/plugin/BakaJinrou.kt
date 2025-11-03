package com.github.tanokun.bakajinrou.plugin

import com.comphenix.protocol.ProtocolLibrary
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.interaction.player.cache.PutPlayerToCacheListener
import com.github.tanokun.bakajinrou.plugin.interaction.player.handling.command.HandleGameCommand
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.command.GameSettingCommand
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.command.MapSettingCommand
import com.github.tanokun.bakajinrou.plugin.module.GameBuilderModule
import com.github.tanokun.bakajinrou.plugin.rendering.tab.authentication.TabListCensor
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.TabHandlerType
import com.github.tanokun.bakajinrou.plugin.rendering.tab.handler.lifecycle.RendererLifecycle
import com.github.tanokun.bakajinrou.plugin.rendering.tab.lobby.LobbyTabRefresher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.ksp.generated.module
import xyz.xenondevs.invui.InvUI

open class BakaJinrou(): JavaPlugin() {
    override fun onLoad() {
        val gameBuilderModule = GameBuilderModule(this)

        startKoin {
            modules(gameBuilderModule.gameBuildScopeModule)
            modules(GameComponentsModule.module)
        }
    }

    override fun onEnable() {
        InvUI.getInstance().setPlugin(this)

        val asyncLoader = AsyncSettingLoader(this)

        val colorPalletDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadColorPallet() }
        val translatorDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadTranslator() }
        val gameMapRegistryDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadMaps() }
        val templatesDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadTemplate() }

        val gameSettings = getKoin().get<GameSettings>()

        getKoin().get<TabHandler>().createEngine(TabHandlerType.ShareInLobby)

        scope.launch {
            val translator = translatorDeferred.await()
            val gameMapRegistry = gameMapRegistryDeferred.await()

            HandleGameCommand(gameSettings, translator)
            MapSettingCommand(gameMapRegistry, scope)
            GameSettingCommand(gameSettings, templatesDeferred.await(), gameMapRegistry, translator, colorPalletDeferred.await())

            Bukkit.getPluginManager().registerEvents(PutPlayerToCacheListener(gameSettings), this@BakaJinrou)

            Bukkit.getPluginManager().registerSuspendingEvents(LobbyTabRefresher(gameSettings, getKoin().get(), translator, getKoin().get(), this), this@BakaJinrou)
            Bukkit.getPluginManager().registerEvents(RendererLifecycle(getKoin().get()), this@BakaJinrou)

            ProtocolLibrary.getProtocolManager().addPacketListener(TabListCensor(getKoin().get(), this@BakaJinrou))
        }

        addQuartzRecipe()

    }

    private fun addQuartzRecipe() {
        val recipeKey = NamespacedKey(this, "custom_end_crystal")
        val result = ItemStack(Material.END_CRYSTAL)
        val recipe = ShapedRecipe(recipeKey, result).apply {
            shape(
                "QQQ",
                "QQQ",
                "QQQ"
            )
            setIngredient('Q', Material.QUARTZ)
        }
        Bukkit.addRecipe(recipe)
    }
}