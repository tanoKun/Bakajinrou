package com.github.tanokun.bakajinrou.plugin.map.result

import com.github.tanokun.bakajinrou.plugin.map.GameMap

/**
 * マップを削除した時の、結果を表すオブジェクトです。
 */
sealed interface MapDeletionResult {
    /**
     * 削除するマップデータが存在しないことを表します。
     */
    data object MapNotFound: MapDeletionResult

    /**
     * 作成が成功したことを表します。
     *
     * @param deletion 削除されたマップ
     */
    data class DeletionSucceeded(val deletion: GameMap): MapDeletionResult
}