package com.github.tanokun.bakajinrou.plugin

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.tanokun.bakajinrou.api.participant.all
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.map.GameMapRegistry
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.game.scheduler.ScheduleState
import com.github.tanokun.bakajinrou.plugin.command.MapSettingCommand
import com.github.tanokun.bakajinrou.plugin.infrastructure.GameMapRepositoryImpl
import com.github.tanokun.bakajinrou.plugin.infrastructure.formatter.ColorPalletRepository
import com.github.tanokun.bakajinrou.plugin.infrastructure.localization.DictionaryRepository
import com.github.tanokun.bakajinrou.plugin.infrastructure.template.PositionTemplateRepository
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.setting.builder.GameBuilderDI
import com.github.tanokun.bakajinrou.plugin.setting.builder.GameComponentSession
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder.AbilityUsersAssigner.Companion.assignAbilityUsers
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder.CitizenAssigner.Companion.assignCitizens
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder.FoxAssigner.Companion.assignFox
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder.IdiotAssigner.Companion.assignIdiots
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder.MadmanAssigner.Companion.assignMadmans
import com.github.tanokun.bakajinrou.plugin.setting.builder.ParticipantBuilder.WolfAssigner.Companion.assignWolfs
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin
import xyz.xenondevs.invui.InvUI
import java.io.File
import java.util.*
import kotlin.time.Duration.Companion.minutes

open class BakaJinrou: JavaPlugin() {
   // private val gameSettings: GameSettings by lazy { GameSettings(this, ProtocolLibrary.getProtocolManager()) }

    private lateinit var jinrouTranslator: JinrouTranslator

    override fun onLoad() {
        val gameBuilderDI = GameBuilderDI(this)

        startKoin {
            modules(gameBuilderDI.gameBuildScopeModule, gameBuilderDI.singleModule, gameBuilderDI.observersModule, gameBuilderDI.providersModule)
            printLogger(Level.DEBUG)
        }
    }

    override fun onEnable() {
        InvUI.getInstance().setPlugin(this)

        val mapRepository = GameMapRepositoryImpl(File(dataFolder, "maps"))
        val gameMapRegistry = GameMapRegistry(mapRepository)

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

            val templateRepository = PositionTemplateRepository(this@BakaJinrou)
            val templates = templateRepository.load()

            launch(minecraftDispatcher) {
              //  GameSettingCommand(gameSettings, templates, gameMapRegistry)
                logger.info("役職テンプレートの読み込みが完了しました。")
            }
        }

        this.scope.launch(Dispatchers.IO) {
            logger.info("カラーパレットを読み込み中...")
            val colorPalletRepository = ColorPalletRepository(this@BakaJinrou)
            val dictionaryRepository = DictionaryRepository(this@BakaJinrou)
            val colorPallet = colorPalletRepository.load()

            launch(minecraftDispatcher) {
                logger.info("カラーパレットの読み込みが完了しました。")
            }

            logger.info("言語設定を読み込み中...")
            val dictionaries = dictionaryRepository.loadAll()
            jinrouTranslator = JinrouTranslator(dictionaries, colorPallet)

            launch(minecraftDispatcher) {
                logger.info("言語設定の読み込みが完了しました。")
            }

        }

        CommandAPICommand("test1")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                getKoin().createScope<GameComponentSession>("name-${UUID.randomUUID()}").apply {
                    declare(this)
                    declare(jinrouTranslator)

                    val builder = get<ParticipantBuilder> { parametersOf(hashMapOf(RequestedPositions.WOLF to 1),
                        Bukkit.getOnlinePlayers().map { it.uniqueId }.toSet()) }

                    val participants = builder.assignMadmans()
                        .assignWolfs(true)
                        .assignIdiots()
                        .assignAbilityUsers()
                        .assignFox()
                        .assignCitizens()
                        .all()

                    val controller = get<JinrouGameController> { parametersOf(participants, scope, 1.minutes) }

                    GameComponentSession(get(), get())


                    controller.launch()

                    controller.mainDispatcherScope.launch {
                        get<GameScheduler>().observe(controller.mainDispatcherScope)
                            .filterIsInstance<ScheduleState.Cancelled>()
                            .collect {
                                this@apply.close()
                            }
                    }
                }
            })
            .register()

     //   HandleGameCommand(gameSettings)

    //    Bukkit.getPluginManager().registerEvents(NonLifecycleEventListener(gameSettings), this)

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