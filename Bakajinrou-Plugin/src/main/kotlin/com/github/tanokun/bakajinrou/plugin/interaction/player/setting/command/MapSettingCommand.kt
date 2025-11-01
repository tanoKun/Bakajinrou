package com.github.tanokun.bakajinrou.plugin.interaction.player.setting.command

import com.github.tanokun.bakajinrou.plugin.common.command.Command
import com.github.tanokun.bakajinrou.plugin.map.GameMap
import com.github.tanokun.bakajinrou.plugin.map.GameMapRegistry
import com.github.tanokun.bakajinrou.plugin.map.MapName
import com.github.tanokun.bakajinrou.plugin.map.PointLocation
import com.github.tanokun.bakajinrou.plugin.map.result.MapCreationResult
import com.github.tanokun.bakajinrou.plugin.map.result.MapDeletionResult
import com.github.tanokun.bakajinrou.plugin.map.result.MapUpdateResult
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
import org.bukkit.Material
import org.bukkit.command.CommandSender
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * マップ設定に関する操作を行う管理者向けコマンド `/mapsetting` を定義します。
 *
 * このコマンドは、ゲームに使用されるマップ情報(スポーン地点、ロビー地点、開始時間、クオーツ配布時間)を
 * 登録・編集・削除するために使用されます。
 * 操作は非同期で行われ、ファイルやデータベースへの書き込みを伴います。
 *
 * ## コマンドの構文
 * ### `/mapsetting create <mapName> <spawn> <lobby>`
 * 新しいマップを作成します。
 * - `mapName`: 作成するマップの名前(文字列)
 * - `spawn`: スポーン地点のロケーション
 * - `lobby`: ロビー地点のロケーション
 *
 * ### `/mapsetting delete <mapName>`
 * 指定されたマップを削除します。
 *
 * ### `/mapsetting update spawn <mapName> <spawn>`
 * 指定マップのスポーン地点を更新します。
 *
 * ### `/mapsetting update lobby <mapName> <lobby>`
 * 指定マップのロビー地点を更新します。
 *
 * ### `/mapsetting update time <mapName> <seconds>`
 * 指定マップのゲーム開始後の制限時間(秒)を変更します。
 *
 * ### `/mapsetting update quartztime <mapName> <seconds>`
 * 指定マップにおいて、ゲーム開始からクオーツを配布するまでの時間(秒)を設定します。
 *
 * ## パーミッション
 * - `bakajinrou.command.mapsetting`
 *
 * @property gameMapRegistry マップの作成・更新・削除を管理するレジストリ
 * @property scope 非同期処理に使用する CoroutineScope
 */
class MapSettingCommand(private val gameMapRegistry: GameMapRegistry, private val scope: CoroutineScope): Command() {
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

                    val map = GameMap(mapName, spawnPoint, lobbyPoint, 15.minutes, Material.STONE)

                    sender.info("「${mapName.name}」マップを作成中...")

                    scope.launch(Dispatchers.IO) {
                        when (gameMapRegistry.create(map)) {
                            MapCreationResult.CreationSucceeded -> sender.info("マップの作成に成功しました。", scope)
                            is MapCreationResult.MapAlreadyExists -> sender.error("既に存在するマップのため、作成出来ませんでした。", scope)
                        }
                    }
                })
            )

            .withSubcommand(CommandAPICommand("delete")
                .withArguments(argumentMapName(gameMapRegistry))
                .executes(CommandExecutor { sender, args ->
                    val mapName = MapName(args["mapName"] as String)

                    sender.info("「${mapName.name}」マップを削除中...")

                    scope.launch(Dispatchers.IO) {
                        when (gameMapRegistry.deleteBy(mapName)) {
                            is MapDeletionResult.DeletionSucceeded -> sender.info("マップの削除に成功しました。", scope)
                            MapDeletionResult.MapNotFound -> sender.error("存在しないマップのため、削除できませんでした。", scope)
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

                        sender.info("「${mapName.name}」マップのスポーンポイントを修正中...")

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

                        sender.info("「${mapName.name}」マップのロビーポイントを修正中...")

                        val map = getMap(mapName, sender) ?: return@CommandExecutor

                        update(map.copy(lobbyPoint = lobbyPoint), sender)
                    })
                )
                .withSubcommand(CommandAPICommand("time")
                    .withArguments(argumentMapName(gameMapRegistry))
                    .withArguments(IntegerArgument("seconds",0, Int.MAX_VALUE))
                    .executes(CommandExecutor { sender, args ->
                        val mapName = MapName(args["mapName"] as String)
                        val seconds = args["seconds"] as Int

                        sender.info("「${mapName.name}」マップの制限時間を修正中...")

                        val map = getMap(mapName, sender) ?: return@CommandExecutor

                        update(map.copy(startTime = seconds.seconds), sender)
                    })
                )
            )
            .register()
    }

    private fun Location.toPoint(): PointLocation = PointLocation(world.name, x.roundToInt(), y.roundToInt(), z.roundToInt())

    private fun argumentMapName(gameMapRegistry: GameMapRegistry) = TextArgument("mapName")
        .replaceSuggestions(ArgumentSuggestions.stringCollection { gameMapRegistry.findAll().map { "\"${it.mapName.name}\"" } })

    private fun update(map: GameMap, sender: CommandSender) {
        scope.launch(Dispatchers.IO) {
            when (gameMapRegistry.update(map)) {
                MapUpdateResult.MapNotFound -> sender.error("存在しないマップのため、修正できませんでした。", scope)
                MapUpdateResult.UpdateSucceeded ->sender.info("マップの修正に成功しました。", scope)
            }
        }
    }

    private fun getMap(mapName: MapName, sender: CommandSender): GameMap? =
        gameMapRegistry.findBy(mapName) ?: let {
            sender.error("存在しないマップのため、修正できませんでした。", scope)

            return null
        }
}