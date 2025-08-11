package com.github.tanokun.bakajinrou.plugin.interaction.player.setting.command

import com.github.tanokun.bakajinrou.api.map.MapName
import com.github.tanokun.bakajinrou.api.translate.PrefixKeys
import com.github.tanokun.bakajinrou.game.map.GameMapRegistry
import com.github.tanokun.bakajinrou.plugin.common.command.Command
import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.common.setting.template.DistributionTemplates
import com.github.tanokun.bakajinrou.plugin.interaction.player.setting.gui.SettingPositionGUI
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
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
import plutoproject.adventurekt.text.style.green
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
class GameSettingCommand(
    gameSettings: GameSettings,
    templates: DistributionTemplates,
    mapRegistry: GameMapRegistry,
    translator: JinrouTranslator,
    colorPallet: ColorPallet,
): Command() {
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
                SettingPositionGUI(gameSettings, templates, translator).open(sender)
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
                        raw { translator.translate(PrefixKeys.SPECTATOR, target.locale()) }
                        text(" ${target.name}")
                    })
                    sender.world.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
                    Bukkit.broadcast(component {
                        text("「") color colorPallet.getColor("spectator").asHexString()
                        raw { target.displayName() }
                        text("」が観戦者になりました。") color colorPallet.getColor("spectator").asHexString()
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
                        text("「") color colorPallet.getColor("spectator").asHexString()
                        raw { target.displayName() }
                        text("」が参加者になりました。") color colorPallet.getColor("spectator").asHexString()
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
                        text("マップが「${mapName.name}」になりました。") color green
                    })

                    gameSettings.selectedMap = gameMap
                })
            )
            .register()
    }
}
