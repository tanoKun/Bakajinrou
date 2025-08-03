package com.github.tanokun.bakajinrou.plugin.command

import com.github.tanokun.bakajinrou.api.map.MapName
import com.github.tanokun.bakajinrou.game.map.GameMapRegistry
import com.github.tanokun.bakajinrou.plugin.gui.setting.SettingPositionGUI
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.participant.position.Positions
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.GlowingNotifier
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.HiddenPositionAnnouncer
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.QuartzDistribute
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.TimeAnnouncer
import com.github.tanokun.bakajinrou.plugin.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.setting.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.template.DistributionTemplates
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.text

/**
 * ゲームの事前設定を行う `/jobs`, `/spectators`, `/map` コマンドを定義します。
 *
 * ゲームを始めるために必要な情報を設定します。
 * 管理者専用のセットアップコマンドです。
 *
 * ## コマンドの構文
 *
 * ### `/jobs`
 * 設定GUIを開き、役職分布を設定します。
 *
 * ### `/spectators set <target>`
 * 指定したプレイヤーを観戦者にします。
 * - `target`: 観戦者ではないプレイヤー名
 *
 * ### `/spectators remove <target>`
 * 指定した観戦者を参加者に戻します。
 * - `target`: 現在観戦者であるプレイヤー名
 *
 * ### `/map select <mapName>`
 * 指定されたマップをゲーム用に選択します。
 * - `mapName`: 登録済みのマップ名
 *
 * ## パーミッション
 * - `bakajinrou.command.gamesetting`
 *
 * @property gameSettings ゲームの設定情報
 */
class GameSettingCommand(gameSettings: GameSettings, templates: DistributionTemplates, mapRegistry: GameMapRegistry): Command() {
    init {
        val nonSpectatorArgument = PlayerArgument("target").replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                Bukkit.getOnlinePlayers()
                    .filterNot { gameSettings.spectators.contains(it.uniqueId) }
                    .map { it.name }
            }
        )

        CommandAPICommand("jobs").withPermission("bakajinrou.command.gamesetting")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                SettingPositionGUI(gameSettings, templates).open(sender)
            })
            .register()

        CommandAPICommand("spectators").withPermission("bakajinrou.command.gamesetting")
            .withSubcommand(CommandAPICommand("set")
                .withArguments(nonSpectatorArgument)
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val target = args["target"] as Player

                    if (gameSettings.spectators.contains(target.uniqueId)) {
                        sender.error("「${target.name}」 は既に観戦者です。")
                        return@PlayerCommandExecutor
                    }

                    gameSettings.addSpectator(target.uniqueId)

                    target.playerListName(component {
                        raw { Positions.Spectator.createDisplayComponent() }
                        text(" ${target.name}")
                    })
                    sender.world.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
                    Bukkit.broadcast(component {
                        text("「") color Positions.Spectator.color.asHexString()
                        raw { target.displayName() }
                        text("」が観戦者になりました。") color Positions.Spectator.color.asHexString()
                    })
                })
            )
            .withSubcommand(CommandAPICommand("remove")
                .withArguments(PlayerArgument("target").replaceSuggestions(
                    ArgumentSuggestions.stringCollection { gameSettings.spectators.mapNotNull { Bukkit.getPlayer(it)?.name } })
                )
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val target = args["target"] as Player

                    if (!gameSettings.spectators.contains(target.uniqueId)) {
                        sender.error("「${target.name}」は観戦者ではありません。")
                        return@PlayerCommandExecutor
                    }

                    gameSettings.removeSpectator(target.uniqueId)

                    target.playerListName((component { text(target.name) }))
                    sender.world.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
                    Bukkit.broadcast(component {
                        text("「") color Positions.Spectator.color.asHexString()
                        raw { target.displayName() }
                        text("」が参加者になりました。") color Positions.Spectator.color.asHexString()
                    })
                })
            )
            .register()

        CommandAPICommand("map").withPermission("bakajinrou.command.gamesetting")
            .withSubcommand(CommandAPICommand("select")
                .withArguments(TextArgument("mapName").replaceSuggestions(ArgumentSuggestions.stringCollection { mapRegistry.findAll().map { "\"${it.mapName.name}\"" } }))
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val mapName = MapName(args["mapName"] as String)
                    val gameMap = mapRegistry.findBy(mapName) ?: let {
                        sender.error("存在しないマップです。")
                        return@PlayerCommandExecutor
                    }

                    sender.world.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
                    Bukkit.broadcast(component {
                        text("マップが「${mapName.name}」になりました。") color Positions.Spectator.color.asHexString()
                    })

                    gameSettings.selectedMap = SelectedMap(
                        gameMap,
                        TimeAnnouncer(BukkitPlayerProvider),
                        QuartzDistribute(BukkitPlayerProvider),
                        GlowingNotifier(BukkitPlayerProvider),
                        HiddenPositionAnnouncer(BukkitPlayerProvider)
                    )
                })
            )
            .register()
    }
}