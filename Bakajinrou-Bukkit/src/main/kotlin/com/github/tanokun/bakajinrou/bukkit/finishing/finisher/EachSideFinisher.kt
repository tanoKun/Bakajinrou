package com.github.tanokun.bakajinrou.bukkit.finishing.finisher

import com.github.tanokun.bakajinrou.api.finishing.GameFinisher
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player

abstract class EachSideFinisher: GameFinisher {
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
        player.sendMessage(Component.text("あなたは勝利した！").color(TextColor.color(0x00FF00)))
    }

    /**
     * 敗北メッセージを送信します。
     */
    protected fun sendLoseMessage(player: Player) {
        player.sendMessage(Component.text("あなたは敗北してしまった。").color(TextColor.color(0xFF0000)))
    }
}