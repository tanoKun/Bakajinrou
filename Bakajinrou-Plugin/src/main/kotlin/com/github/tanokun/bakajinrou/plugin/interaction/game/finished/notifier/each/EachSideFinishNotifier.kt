package com.github.tanokun.bakajinrou.plugin.interaction.game.finished.notifier.each

import com.github.tanokun.bakajinrou.plugin.interaction.game.finished.notifier.GameFinishNotifier
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player

abstract class EachSideFinishNotifier(private val translator: JinrouTranslator): GameFinishNotifier {
    /**
     * "[text]の勝利" のフォーマットのもと、勝利タイトルを表示します。
     */
    protected fun showVictorySideTitle(player: Player, text: Component) {
        val victoryTitle = Title.title(
            text,
            Component.text("")
        )

        player.showTitle(victoryTitle)
    }

    /**
     * 勝利メッセージを送信します。
     */
    protected fun sendVictoryMessage(player: Player) {
        player.sendMessage(translator.translate(GameKeys.Finish.VICTORY_MESSAGE, player.locale()))
    }

    /**
     * 敗北メッセージを送信します。
     */
    protected fun sendLoseMessage(player: Player) {
        player.sendMessage(translator.translate(GameKeys.Finish.LOSE_MESSAGE, player.locale()))
    }
}