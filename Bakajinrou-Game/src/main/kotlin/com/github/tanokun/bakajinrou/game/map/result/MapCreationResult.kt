package com.github.tanokun.bakajinrou.game.map.result

import com.github.tanokun.bakajinrou.api.map.GameMap

/**
 * マップを新規作成した時の、結果を表すオブジェクトです。
 */
sealed interface MapCreationResult {
    /**
     * 既に同じ名前で存在してることを表します。
     *
     * @param existing 見つかったマップデータ
     */
    data class MapAlreadyExists(val existing: GameMap): MapCreationResult

    /**
     * 作成が成功したことを表します。
     */
    data object CreationSucceeded: MapCreationResult
}