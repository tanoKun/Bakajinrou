package com.github.tanokun.bakajinrou.plugin

import com.comphenix.protocol.ProtocolLibrary
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.game.map.GameMapRegistry
import com.github.tanokun.bakajinrou.plugin.cache.PlayerSkinCache
import com.github.tanokun.bakajinrou.plugin.command.GameSettingCommand
import com.github.tanokun.bakajinrou.plugin.command.HandleGameCommand
import com.github.tanokun.bakajinrou.plugin.command.MapSettingCommand
import com.github.tanokun.bakajinrou.plugin.infrastructure.GameMapRepositoryImpl
import com.github.tanokun.bakajinrou.plugin.infrastructure.template.PositionTemplateRepository
import com.github.tanokun.bakajinrou.plugin.listener.always.NonLifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.setting.GameSettings
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import xyz.xenondevs.invui.InvUI
import java.io.File

open class BakaJinrou: JavaPlugin() {
    private val gameSettings: GameSettings by lazy { GameSettings(this, ProtocolLibrary.getProtocolManager()) }

    override fun onEnable() {
        InvUI.getInstance().setPlugin(this)

        val mapRepository = GameMapRepositoryImpl(File(dataFolder, "maps"))
        val gameMapRegistry = GameMapRegistry(mapRepository)
        val templateRepository = PositionTemplateRepository(this@BakaJinrou)

        this.scope.launch(Dispatchers.IO) {
            logger.info("人狼マップを読み込み中...")

            gameMapRegistry.loadAllMapsFromRepository()

            launch(minecraftDispatcher) {
                MapSettingCommand(gameMapRegistry, scope)
                logger.info("人狼マップの読み込みが完了しました。")
            }
        }

        this.scope.launch(Dispatchers.IO) {
            logger.info("役職テンプレートを読み込み中...")

            val templates = templateRepository.load()

            launch(minecraftDispatcher) {
                GameSettingCommand(gameSettings, templates, gameMapRegistry)
                logger.info("役職テンプレートの読み込みが完了しました。")
            }
        }

        HandleGameCommand(gameSettings)

        Bukkit.getPluginManager().registerEvents(NonLifecycleEventListener(gameSettings), this)

        addQuartzRecipe()

        CommandAPICommand("test2")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                Bukkit.getOnlinePlayers().forEach {
                    PlayerSkinCache.put(it.uniqueId, it.playerProfile)
                    gameSettings.addCandidate(it.uniqueId)
                }
            })
            .register()
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