package com.github.tanokun.bakajinrou.plugin.setting.builder.game

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.game.controller.AttackController
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import com.github.tanokun.bakajinrou.game.logger.GameLogger
import com.github.tanokun.bakajinrou.game.scheduler.GameScheduler
import com.github.tanokun.bakajinrou.plugin.finisher.CitizenSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.FoxSideFinisher
import com.github.tanokun.bakajinrou.plugin.finisher.WolfSideFinisher
import com.github.tanokun.bakajinrou.plugin.logger.JinrouLogger
import com.github.tanokun.bakajinrou.plugin.logger.body.BukkitBodyHandler
import com.github.tanokun.bakajinrou.plugin.method.BukkitItemFactory
import com.github.tanokun.bakajinrou.plugin.scheduler.JinrouGameScheduler
import com.github.tanokun.bakajinrou.plugin.setting.SelectedMap
import org.bukkit.plugin.Plugin

class CreateLineBuilderDsl(
    private val contexts: DIContext
) {
    fun createGameLogger(
        provider: () -> GameLogger = {
            JinrouLogger()
        }
    ): BodyHandlerCreator {
        contexts.register(provider())

        return BodyHandlerCreatorImpl()
    }

    private inner class BodyHandlerCreatorImpl: BodyHandlerCreator() {
        override fun createBodyHandler(
            provider: () -> BodyHandler
        ): GameSchedulerCreator {
            contexts.register(provider())

            return GameSchedulerCreatorImpl()
        }
    }

    private inner class GameSchedulerCreatorImpl: GameSchedulerCreator() {
        override fun createGameScheduler(provider: () -> GameScheduler): JinrouGameCreator {
            contexts.register(provider())
            return JinrouGameCreatorImpl()
        }
    }

    private inner class JinrouGameCreatorImpl: JinrouGameCreator() {
        override fun createJinrouGame(provider: () -> JinrouGame): ItemFactoryCreator {
            contexts.register(provider())
            return ItemFactoryCreatorImpl()
        }
    }

    private inner class ItemFactoryCreatorImpl: ItemFactoryCreator() {
        override fun createItemFactory(provider: () -> BukkitItemFactory): ControllersCreator {
            contexts.register(provider())

            return ControllersCreatorImpl()
        }
    }

    private inner class ControllersCreatorImpl: ControllersCreator() {
        override fun createControllers(
            gameController: () -> JinrouGameController,

            attackController: () -> AttackController
        ): ListenersRegisterDsl {
            contexts.apply {
                register(gameController())
                register(attackController())
            }


            return ListenersRegisterDsl(contexts)
        }
    }

    abstract inner class ItemFactoryCreator() {

        abstract fun createItemFactory(provider: () -> BukkitItemFactory = { BukkitItemFactory(contexts.get()) }): ControllersCreator
    }

    abstract inner class GameSchedulerCreator {
        abstract fun createGameScheduler(
            provider: () -> GameScheduler = {
                JinrouGameScheduler(
                    startTime = contexts.get<GameMap>().startTime.inWholeSeconds,
                    timeSchedules = contexts.get<SelectedMap>().createSchedules(participants = contexts.get()),
                    bukkitScheduler = contexts.get(),
                    plugin = contexts.get()
                )
            }
        ): JinrouGameCreator
    }

    abstract inner class JinrouGameCreator {

        abstract fun createJinrouGame(
            provider: () -> JinrouGame = {
                JinrouGame(
                    participants = contexts.get(),
                    { CitizenSideFinisher(it) },
                    { WolfSideFinisher(it) },
                    { FoxSideFinisher(it) }
                )
            }
        ): ItemFactoryCreator
    }

    abstract inner class ControllersCreator {

        abstract fun createControllers(
            gameController: () -> JinrouGameController = {
                JinrouGameController(
                    game = contexts.get(),
                    scheduler = contexts.get(),
                    debug = contexts.get<Plugin>().logger,
                    uiDispatcher = contexts.get<Plugin>().minecraftDispatcher
                )
            },
            attackController: () -> AttackController = {
                AttackController(
                    gameLogger = contexts.get(),
                    bodyHandler = contexts.get(),
                    debug = contexts.get(),
                    game = contexts.get(),
                    gameController = contexts.get(),
                )
            }
        ): ListenersRegisterDsl
    }

    abstract inner class  BodyHandlerCreator {
        abstract fun createBodyHandler(
            provider: () -> BodyHandler = {
                BukkitBodyHandler(server = contexts.get(), participants = contexts.get())
            }
        ): GameSchedulerCreator
    }

}
