package com.github.tanokun.bakajinrou.game.map.result

/**
 * マップを上書きした時の、結果を表すオブジェクトです。
 */
sealed interface MapUpdateResult {
    /**
     * 上書きするマップデータが存在しないことを表します。
     */
    data object MapNotFound: MapUpdateResult

    /**
     * 作成が成功したことを表します。
     */
    data object UpdateSucceeded: MapUpdateResult
}