package com.github.tanokun.bakajinrou.plugin.setting.factory

import com.github.tanokun.bakajinrou.game.scheduler.schedule.TimeSchedule
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.GrowingNotifier
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.HiddenPositionAnnouncer
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.QuartzDistribute
import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.TimeAnnouncer
import com.github.tanokun.bakajinrou.plugin.setting.map.GameMap

/**
 * ユーザーにより選択されたマップをラップし、スケジュール生成などの依存を注入した形で提供します。
 *
 * @property gameMap 選択されたゲームマップ
 * @property timeAnnouncer 残り時間の通知処理を担うクラス
 * @property quartzDistribute クォーツ配布処理を行うクラス。
 * @property growingNotifier 市民の発光などを通知するクラス
 * @property hiddenPositionAnnouncer 秘匿役職の情報を開示するクラス
 */
class SelectedMap(
    private val gameMap: GameMap,
    private val timeAnnouncer: TimeAnnouncer,
    private val quartzDistribute: QuartzDistribute,
    private val growingNotifier: GrowingNotifier,
    private val hiddenPositionAnnouncer: HiddenPositionAnnouncer,
) {

    val startTime = gameMap.startTime

    fun createSchedules(): List<TimeSchedule> = gameMap.createSchedules(timeAnnouncer, quartzDistribute, growingNotifier, hiddenPositionAnnouncer)
}