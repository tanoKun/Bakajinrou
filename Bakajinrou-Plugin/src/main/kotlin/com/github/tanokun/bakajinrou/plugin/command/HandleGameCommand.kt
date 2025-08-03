package com.github.tanokun.bakajinrou.plugin.command

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.finisher.SystemFinisher
import com.github.tanokun.bakajinrou.plugin.setting.GameBuildResult
import com.github.tanokun.bakajinrou.plugin.setting.GameSettings
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
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
class HandleGameCommand(gameSettings: GameSettings): Command() {
    private var currentController: JinrouGameController? = null

    private var currentGame: JinrouGame? = null

    init {
        CommandAPICommand("start").withPermission("bakajinrou.command.gamesetting")
            .executes(CommandExecutor { sender, args ->
                val (game, controller) = when (val result = gameSettings.buildGame()) {
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

                        result.jinrouGame to result.gameController
                    }
                }

                currentController = controller.apply {
                    launch()
                }

                currentGame = game
            })
            .register()

        CommandAPICommand("reset").withPermission("bakajinrou.command.gamesetting")
            .executes(CommandExecutor { sender, args ->
                val controller = currentController ?: let {
                    sender.error("ゲームが行われていません。")
                    return@CommandExecutor
                }

                val jinrouGame = currentGame ?: return@CommandExecutor

                controller.finish(SystemFinisher(jinrouGame.getAllParticipants()))

                this.currentGame = null
                this.currentController = null
            })
            .register()
    }
}