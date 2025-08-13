package com.github.tanokun.bakajinrou.plugin.system.game.finished.observing

import com.github.tanokun.bakajinrou.api.WonInfo
import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.observing.Observer
import com.github.tanokun.bakajinrou.game.attacking.BodyHandler
import com.github.tanokun.bakajinrou.game.session.JinrouGameSession
import com.github.tanokun.bakajinrou.plugin.adapter.bukkit.player.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.system.game.launched.asBukkit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.bukkit.GameMode

/**
 * ゲームの終了を監視し、勝利条件が確定した際に共通する終了処理を行います。
 *
 * このオブザーバーのライフサイクルは、ゲームの持つライフサイクルではなく、その上のライフサイクルを持ちます。
 * つまり、ゲームが終了されてもプレイヤーの処理が終わらない限り、処理が完了することはありません。
 *
 * 監視対象が "ゲーム終了" であるため、その時点で監視は自動終了します。
 *
 * @property playerProvider BukkitのPlayerオブジェクトを取得するための Provider
 * @property game 監視対象のゲームセッション
 * @property gameMap ゲームマップ情報
 * @property topScope このクラスの全てのコルーチンが動作する、上位のコルーチンスコープ
 */
class CommonGameFinisher(
    private val playerProvider: BukkitPlayerProvider,
    private val game: JinrouGameSession,
    private val gameMap: GameMap,
    private val topScope: CoroutineScope,
    private val bodyHandler: BodyHandler
): Observer {
    init {
        topScope.launch {
            game.observeWin(topScope)
                .take(1)
                .collect(::atFinish)
        }
    }

    /**
     * ゲーム終了時の後片付け処理を、参加者ごとに並行して実行します。
     *
     * @param wonInfo 勝利チームと、それに含まれる参加者の情報
     */
    private fun atFinish(wonInfo: WonInfo) {
        bodyHandler.deleteBodies()

        wonInfo.participants.forEach { participant ->
            topScope.launch {
                val player = playerProvider.waitPlayerOnline(participant.participantId)
                player.inventory.clear()

                player.gameMode = GameMode.ADVENTURE
                player.teleport(gameMap.lobbyPoint.asBukkit())
            }
        }
    }
}