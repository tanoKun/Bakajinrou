package com.github.tanokun.bakajinrou.plugin.command

import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.map.MapName
import com.github.tanokun.bakajinrou.api.map.PointLocation
import com.github.tanokun.bakajinrou.game.map.GameMapRegistry
import com.github.tanokun.bakajinrou.game.map.result.MapCreationResult
import com.github.tanokun.bakajinrou.game.map.result.MapDeletionResult
import com.github.tanokun.bakajinrou.game.map.result.MapUpdateResult
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.command.CommandSender
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.red
import plutoproject.adventurekt.text.text
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MapSettingCommand(private val gameMapRegistry: GameMapRegistry, private val scope: CoroutineScope) {
    init {
        CommandAPICommand("mapsetting").withPermission("bakajinrou.command.mapsetting")
            .withSubcommand(CommandAPICommand("create")
                .withArguments(TextArgument("mapName"))
                .withArguments(LocationArgument("spawn"))
                .withArguments(LocationArgument("lobby"))
                .executes(CommandExecutor { sender, args ->
                    val mapName = MapName(args["mapName"] as String)
                    val spawnPoint = (args["spawn"] as Location).toPoint()
                    val lobbyPoint = (args["lobby"] as Location).toPoint()

                    val map = GameMap(mapName, spawnPoint, lobbyPoint, 15.minutes, 3.minutes)

                    sender.sendMessage(component {
                        text("「${mapName.name}」マップを作成中...") color gray deco bold
                    })

                    scope.launch(Dispatchers.IO) {
                        val message = when (gameMapRegistry.create(map)) {
                            MapCreationResult.CreationSucceeded ->
                                component { text("マップの作成に成功しました。") color gray deco bold }
                            is MapCreationResult.MapAlreadyExists ->
                                component { text("既に存在するマップのため、作成出来ませんでした。") color red deco bold }
                        }

                        scope.launch {
                            sender.sendMessage(message)
                        }
                    }
                })
            )

            .withSubcommand(CommandAPICommand("delete")
                .withArguments(argumentMapName(gameMapRegistry))
                .executes(CommandExecutor { sender, args ->
                    val mapName = MapName(args["mapName"] as String)

                    sender.sendMessage(component {
                        text("「${mapName.name}」マップを削除中...") color gray deco bold
                    })

                    scope.launch(Dispatchers.IO) {
                        val message = when (gameMapRegistry.deleteBy(mapName)) {
                            is MapDeletionResult.DeletionSucceeded ->
                                component { text("マップの削除に成功しました。") color gray deco bold }
                            MapDeletionResult.MapNotFound ->
                                component { text("存在しないマップのため、削除できませんでした。") color red deco bold }
                        }

                        scope.launch {
                            sender.sendMessage(message)
                        }
                    }
                })
            )

            .withSubcommand(CommandAPICommand("update")
                .withSubcommand(CommandAPICommand("spawn")
                    .withArguments(argumentMapName(gameMapRegistry))
                    .withArguments(LocationArgument("spawn"))
                    .executes(CommandExecutor { sender, args ->
                        val mapName = MapName(args["mapName"] as String)
                        val spawnPoint = (args["spawn"] as Location).toPoint()

                        sender.sendMessage(component {
                            text("「${mapName.name}」マップのスポーンポイントを修正中...") color gray deco bold
                        })

                        val map = getMap(mapName, sender) ?: return@CommandExecutor

                        update(map.copy(spawnPoint = spawnPoint), sender)
                    })
                )
                .withSubcommand(CommandAPICommand("lobby")
                    .withArguments(argumentMapName(gameMapRegistry))
                    .withArguments(LocationArgument("lobby"))
                    .executes(CommandExecutor { sender, args ->
                        val mapName = MapName(args["mapName"] as String)
                        val lobbyPoint = (args["lobby"] as Location).toPoint()

                        sender.sendMessage(component {
                            text("「${mapName.name}」マップのロビーポイントを修正中...") color gray deco bold
                        })

                        val map = getMap(mapName, sender) ?: return@CommandExecutor

                        update(map.copy(lobbyPoint = lobbyPoint), sender)
                    })
                )
                .withSubcommand(CommandAPICommand("starttime")
                    .withArguments(argumentMapName(gameMapRegistry))
                    .withArguments(IntegerArgument("seconds",0, Int.MAX_VALUE))
                    .executes(CommandExecutor { sender, args ->
                        val mapName = MapName(args["mapName"] as String)
                        val seconds = args["seconds"] as Int

                        sender.sendMessage(component {
                            text("「${mapName.name}」マップの制限時間を修正中...") color gray deco bold
                        })

                        val map = getMap(mapName, sender) ?: return@CommandExecutor

                        update(map.copy(startTime = seconds.seconds), sender)
                    })
                )
                .withSubcommand(CommandAPICommand("quartztime")
                    .withArguments(argumentMapName(gameMapRegistry))
                    .withArguments(IntegerArgument("seconds",0, Int.MAX_VALUE))
                    .executes(CommandExecutor { sender, args ->
                        val mapName = MapName(args["mapName"] as String)
                        val seconds = args["seconds"] as Int

                        sender.sendMessage(component {
                            text("「${mapName.name}」マップのクオーツ配布時間を修正中...") color gray deco bold
                        })

                        val map = getMap(mapName, sender) ?: return@CommandExecutor

                        update(map.copy(delayToGiveQuartz = seconds.seconds), sender)
                    })
                )
            )
            .register()
    }

    private fun Location.toPoint(): PointLocation = PointLocation(world.name, x.roundToInt(), y.roundToInt(), z.roundToInt())

    private fun argumentMapName(gameMapRegistry: GameMapRegistry) = TextArgument("mapName")
        .replaceSuggestions(ArgumentSuggestions.stringCollection { gameMapRegistry.findAll().map { it.mapName.name } })

    private fun update(map: GameMap, sender: CommandSender) {
        scope.launch(Dispatchers.IO) {
            val message = when (gameMapRegistry.update(map)) {
                MapUpdateResult.MapNotFound ->
                    component { text("存在しないマップのため、修正できませんでした。") color red deco bold }
                MapUpdateResult.UpdateSucceeded ->
                    component { text("マップの修正に成功しました。") color gray deco bold }
            }

            scope.launch {
                sender.sendMessage(message)
            }
        }
    }

    private fun getMap(mapName: MapName, sender: CommandSender): GameMap? =
        gameMapRegistry.findBy(mapName) ?: let {
            sender.sendMessage(component {
                component { text("存在しないマップのため、修正できませんでした。") color red deco bold }
            })

            return null
        }
}