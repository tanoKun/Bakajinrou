package com.github.tanokun.bakajinrou.plugin

import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.cache.PlayerSkinCache
import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.common.setting.builder.GameBuilderDI
import com.github.tanokun.bakajinrou.plugin.interaction.player.handle.command.HandleGameCommand
import com.github.tanokun.bakajinrou.plugin.interaction.player.initializer.NonLifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.command.GameSettingCommand
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.command.MapSettingCommand
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
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
import xyz.xenondevs.invui.InvUI

open class BakaJinrou(): JavaPlugin() {
    private val gameSettings: GameSettings by lazy { GameSettings(this) }

    override fun onLoad() {
        val gameBuilderDI = GameBuilderDI(this)

        startKoin {
            modules(gameBuilderDI.gameBuildScopeModule)
            printLogger(org.koin.core.logger.Level.DEBUG)
        }
    }

    override fun onEnable() {
        InvUI.getInstance().setPlugin(this)

        val asyncLoader = AsyncSettingLoader(this)

        val colorPalletDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadColorPallet() }
        val translatorDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadTranslator() }
        val gameMapRegistryDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadMaps() }
        val templatesDeferred = scope.async(Dispatchers.IO) { asyncLoader.loadTemplate() }

        scope.launch {
            HandleGameCommand(gameSettings, translatorDeferred.await())
            MapSettingCommand(gameMapRegistryDeferred.await(), scope)
            GameSettingCommand(
                gameSettings, templatesDeferred.await(), gameMapRegistryDeferred.await(), translatorDeferred.await(), colorPalletDeferred.await()
            )

            Bukkit.getPluginManager().registerEvents(NonLifecycleEventListener(gameSettings, translatorDeferred.await()), this@BakaJinrou)
        }

        CommandAPICommand("test1")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                Bukkit.getOnlinePlayers().forEach {
                    PlayerNameCache.put(it.uniqueId, it.name)
                    PlayerSkinCache.put(it.uniqueId, it.playerProfile)

                    gameSettings.addCandidate(it.uniqueId)
                }
            })
            .register()


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

fun debug() {
    println(Thread.currentThread().stackTrace[1])
}