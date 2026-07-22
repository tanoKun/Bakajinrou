package com.github.tanokun.bakajinrou.plugin

import com.comphenix.protocol.ProtocolLibrary
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.plugin.player.cache.PutPlayerToCacheListener
import com.github.tanokun.bakajinrou.plugin.bootstrap.GameBuilderModule
import com.github.tanokun.bakajinrou.plugin.presentation.tab.authentication.TabListCensor
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandler
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.TabHandlerType
import com.github.tanokun.bakajinrou.plugin.presentation.tab.handler.lifecycle.RendererLifecycle
import com.github.tanokun.bakajinrou.plugin.presentation.tab.lobby.LobbyTabRefresher
import com.github.tanokun.bakajinrou.plugin.setting.command.MapSettingCommand
import com.github.tanokun.bakajinrou.plugin.presentation.prepare.PreparedGamePresentationAdapter
import com.github.tanokun.bakajinrou.plugin.setting.prepare.command.PrepareCommand
import com.github.tanokun.bakajinrou.plugin.setting.start.adapter.StartGameAdapter
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

        val koin = getKoin()

        koin.get<TabHandler>().createEngine(TabHandlerType.ShareInLobby)

        scope.launch {
            koin.declare(colorPalletDeferred.await())
            koin.declare(translatorDeferred.await())
            koin.declare(gameMapRegistryDeferred.await())
            koin.declare(templatesDeferred.await())

            koin.get<PrepareCommand>()
            koin.get<MapSettingCommand>()

            Bukkit.getPluginManager().registerEvents(
                PreparedGamePresentationAdapter(koin.get(), koin.get(), koin.get(), koin.get()),
                this@BakaJinrou,
            )
            Bukkit.getPluginManager().registerEvents(StartGameAdapter(koin.get()), this@BakaJinrou)

            Bukkit.getPluginManager().registerEvents(PutPlayerToCacheListener(), this@BakaJinrou)

            Bukkit.getPluginManager().registerSuspendingEvents(LobbyTabRefresher(koin.get()), this@BakaJinrou)
            Bukkit.getPluginManager().registerEvents(RendererLifecycle(koin.get()), this@BakaJinrou)

            ProtocolLibrary.getProtocolManager().addPacketListener(TabListCensor(koin.get(), this@BakaJinrou))
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
