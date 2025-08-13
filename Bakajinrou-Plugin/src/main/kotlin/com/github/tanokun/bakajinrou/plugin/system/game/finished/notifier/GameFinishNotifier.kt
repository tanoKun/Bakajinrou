package com.github.tanokun.bakajinrou.plugin.system.game.finished.notifier

import com.github.tanokun.bakajinrou.api.WonInfo

/**
 * ゲーム終了時の勝利情報を通知します。
 *
 * 実装クラスは、特定の勝利条件や勝利サイドに応じて
 * プレイヤーへの表示やメッセージ送信などの処理を行います。
 */
interface GameFinishNotifier {

    /**
     * ゲーム終了の勝利情報を通知します。
     *
     * @param wonInfo 勝利した側や勝者に関する情報
     */
    fun notify(wonInfo: WonInfo)
}