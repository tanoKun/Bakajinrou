//package com.github.tanokun.bakajinrou.plugin.setting
//
//import com.github.tanokun.bakajinrou.api.map.GameMap
//import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
//import com.github.tanokun.bakajinrou.game.scheduler.schedule.TimeSchedule
//import com.github.tanokun.bakajinrou.game.scheduler.schedule.arranged
//import com.github.tanokun.bakajinrou.game.scheduler.schedule.every
//import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.GlowingNotifier
//import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.HiddenPositionAnnouncer
//import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.QuartzDistribute
//import com.github.tanokun.bakajinrou.plugin.scheduler.schedule.TimeAnnouncer
//import kotlin.time.Duration.Companion.minutes
//import kotlin.time.Duration.Companion.seconds
//
///**
// * ユーザーにより選択されたマップをラップし、スケジュール生成などの依存を注入した形で提供します。
// *
// * @property gameMap 選択されたマップ
// * @property timeAnnouncer 残り時間の通知処理を担うクラス
// * @property quartzDistribute クォーツ配布処理を行うクラス
// * @property glowingNotifier 市民の発光などを通知するクラス
// * @property hiddenPositionAnnouncer 秘匿役職の情報を開示するクラス
// */
//class SelectedMap(
//    val gameMap: GameMap,
//    private val timeAnnouncer: TimeAnnouncer,
//    private val quartzDistribute: QuartzDistribute,
//    private val glowingNotifier: GlowingNotifier,
//    private val hiddenPositionAnnouncer: HiddenPositionAnnouncer,
//) {
//
//    fun createSchedules(participants: ParticipantScope.All): List<TimeSchedule> = listOf(
//        1.seconds every { leftSeconds ->
//            timeAnnouncer.showRemainingTimeActionBar(participants, leftSeconds)
//        },
//
//        gameMap.delayToGiveQuartz every { leftSeconds ->
//            quartzDistribute.distributeQuartzToSurvivors(participants)
//
//            if (gameMap.startTime.inWholeSeconds == leftSeconds) quartzDistribute.distributeQuartzToSurvivors(participants)
//        },
//
//        6.minutes arranged {
//            glowingNotifier.announceGlowingStart(participants, 3)
//        },
//
//        1.minutes every schedule@ { leftSeconds ->
//            if (leftSeconds.seconds > 5.minutes) return@schedule
//
//            glowingNotifier.glowCitizens(participants)
//        },
//
//        3.minutes arranged {
//            hiddenPositionAnnouncer.notifyWolfsAndFox(participants)
//        }
//    )
//}