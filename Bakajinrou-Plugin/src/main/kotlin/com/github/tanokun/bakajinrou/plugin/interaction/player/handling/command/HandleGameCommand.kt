package com.github.tanokun.bakajinrou.plugin.interaction.player.handling.command

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.common.command.Command
import com.github.tanokun.bakajinrou.plugin.common.setting.GameBuildResult
import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.Bukkit

/**
 * ゲームの開始・リセットを制御する `/start`, `/reset` コマンドを定義します。
 *
 * ゲームの開始・停止を行います。
 *
 * ## コマンドの構文
 *
 * ### `/start`
 * ゲームを開始します。以下の条件を満たす必要があります。
 * - 適切な役職分布が設定されている
 * - 使用するマップが選択されている
 *
 * ### `/reset`
 * 実行中のゲームを強制終了します。
 *
 * ### パーミッション
 * - `bakajinrou.command.gamesetting`
 *
 * @property gameSettings ゲームに必要な構成
 */

class HandleGameCommand(gameSettings: GameSettings, translator: JinrouTranslator): Command() {
    private var currentSession: JinrouGameSession? = null
    private var currentGame: JinrouGame? = null

    init {
        CommandAPICommand("start").withPermission("bakajinrou.command.gamesetting")
            .executes(CommandExecutor { sender, args ->
                if (currentSession?.isFinished() == false) {
                    sender.error("ゲームが行われています。")
                    return@CommandExecutor
                }

                Bukkit.getOnlinePlayers().forEach { it.playerListName(it.playerListName()) }

                val (game, session) = when (val result = gameSettings.buildGameSession(translator)) {
                    GameBuildResult.IllegalSelectedPositions -> {
                        sender.error("参加者の人数と、役職が合っていません。")
                        return@CommandExecutor
                    }

                    GameBuildResult.NotFoundSettingMap -> {
                        sender.error("マップが選択されていません。")
                        return@CommandExecutor
                    }

                    is GameBuildResult.SucceedCreation -> {
                        sender.info("ゲームを開始します...")

                        result.game to result.gameSession
                    }
                }

                this.currentSession = session.apply {
                    launch()
                }

                this.currentGame = game
            })
            .register()

        CommandAPICommand("reset").withPermission("bakajinrou.command.gamesetting")
            .executes(CommandExecutor { sender, args ->
                val session = currentSession ?: let {
                    sender.error("ゲームが行われていません。")
                    return@CommandExecutor
                }

                session.notifyWonBySystem()

                this.currentSession = null
                this.currentGame = null
            })
            .register()
    }
}