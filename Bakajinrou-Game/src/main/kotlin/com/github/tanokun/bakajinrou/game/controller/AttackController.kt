package com.github.tanokun.bakajinrou.game.controller

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.attack.AttackResult
import com.github.tanokun.bakajinrou.api.attack.method.effect.DamagePotionEffect
import com.github.tanokun.bakajinrou.api.attack.method.item.SwordItem
import com.github.tanokun.bakajinrou.api.attack.method.other.ArrowMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.logger.BodyHandler
import com.github.tanokun.bakajinrou.game.logger.DebugLogger
import com.github.tanokun.bakajinrou.game.logger.GameLogger

/**
 * 攻撃手段ごとの攻撃を統括処理するコントローラーです。
 *
 * 実際の攻撃は、各 [com.github.tanokun.bakajinrou.api.method.AttackMethod] に委譲します。
 * 攻撃後には攻撃手段の消費処理を行い、使用済みの手段を攻撃者から剥奪します。
 * また、攻撃結果はすべてデバッグログとして記録されます。
 *
 * このクラスの責務：
 * - 各攻撃手段に攻撃処理を委譲する
 * - 使用済みの手段を消費、剥奪する
 * - 攻撃結果のログを記録する

 * @param debug 攻撃結果のログ記録に使用する
 *
 * @see com.github.tanokun.bakajinrou.api.method.AttackMethod.attack
 * @see SwordItem
 * @see ArrowMethod
 * @see DamagePotionEffect
 */
class AttackController(
    private val gameLogger: GameLogger,
    private val bodyHandler: BodyHandler,
    private val debug: DebugLogger,
    private val game: JinrouGame,
    private val gameController: JinrouGameController
) {
    /**
     * [victim] の死亡状態に伴う副作用を実行します。
     * この関数は、[victim] が死んだ後に呼び出されることを想定しています。
     *
     * 前提条件:
     * - [victim]、[by] がゲームの参加者であること
     * - [victim] が死亡状態であること
     *
     * 副作用:
     * - キルログの出力
     * - 死体エンティティを生成
     * - ゲームが終了条件を満たした場合、ゲームを終了する
     *
     * @param victim 殺された参加者
     * @param by 殺した参加者
     */
    fun notifyDeath(by: Participant, victim: Participant) {
        if (!game.participants.contains(victim)) return
        if (!game.participants.contains(by)) return

        if (victim.state != ParticipantStates.DEAD) return

        gameLogger.logKillParticipantToSpectator(victim.uniqueId, by.uniqueId)
        bodyHandler.createBody(victim.uniqueId)

        debug.logKill(by.uniqueId, victim.uniqueId)

        game.judge()?.let { finisher ->
            gameController.finish(finisher)
        }
    }

    /**
     * 剣によって [by] が [victim] へ攻撃します。
     *
     * 攻撃自体は全て [with] (攻撃手段) に委ねられます。
     * また、[with] による攻撃では、[victim] の防御手段への副作用が起こる可能性があります。
     *
     * 副作用：
     * - [with] による攻撃処理の実行
     * - [with] の消費
     * - 使用された [with] の剥奪
     * - 攻撃結果のデバッグログを出力する
     *
     * @param by 攻撃を行う参加者
     * @param victim 攻撃される参加者
     * @param with 攻撃手段として使用する [SwordItem]
     *
     * @see com.github.tanokun.bakajinrou.api.method.AttackMethod.attack
     * @see SwordItem
     */
    fun attack(by: Participant, victim: Participant, with: SwordItem) {
        val result = with.attack(victim = victim)
        with.onConsume(consumer = by)
        by.removeMethod(method = with)

        debug.logAttackResult(by.uniqueId, victim.uniqueId, with, result)

        if (result == AttackResult.SuccessAttack) notifyDeath(by = by, victim = victim)
    }

    /**
     * 弓によって [by] が [victim] へ攻撃します。
     *
     * 攻撃自体は全て [with] (攻撃手段) に委ねられます。
     * また、[with] による攻撃では、[victim] の防御手段への副作用が起こる可能性があります。
     *
     * 副作用：
     * - [with] による攻撃処理の実行
     * - 使用された [with] の剥奪
     * - 攻撃結果のデバッグログを出力する
     *
     * @param by 攻撃を行う参加者
     * @param victim 攻撃される参加者
     * @param with 攻撃手段として使用する [ArrowMethod]
     *
     * @see com.github.tanokun.bakajinrou.api.method.AttackMethod.attack
     * @see ArrowMethod
     */
    fun attack(by: Participant, victim: Participant, with: ArrowMethod) {
        val result = with.attack(victim = victim)
        by.removeMethod(with)

        debug.logAttackResult(by.uniqueId, victim.uniqueId, with, result)

        if (result == AttackResult.SuccessAttack) notifyDeath(by = by, victim = victim)
    }

    /**
     * ダメージポーションによって [by] が [victims] へ攻撃します。
     *
     * 攻撃自体は全て [with] (攻撃手段) に委ねられます。
     * また、[with] による攻撃では、[victims] の防御手段への副作用が起こる可能性があります。
     *
     * 副作用：
     * - [with] による攻撃処理の実行
     * - 使用された [with] の剥奪
     * - 攻撃結果のデバッグログを出力する
     *
     * @param by 攻撃を行う参加者
     * @param victims 攻撃される参加者
     * @param with 攻撃手段として使用する [ArrowMethod]
     *
     * @see com.github.tanokun.bakajinrou.api.method.AttackMethod.attack
     * @see DamagePotionEffect
     */
    fun attack(by: Participant, victims: List<Participant>, with: DamagePotionEffect) {
        victims.forEach {
            val result = with.attack(victim = it)

            debug.logAttackResult(by.uniqueId, it.uniqueId, with, result)

            if (result == AttackResult.SuccessAttack) notifyDeath(by = by, victim = it)
        }


        by.removeMethod(with)
    }

    /**
     * 弓が [with] を持つ矢を放ったことを表します。
     * この際、手段として消費されますが、剥奪はされません。
     *
     * @param shooter 引いた参加者
     * @param with 矢
     */
    fun shootArrow(shooter: Participant, with: ArrowMethod) {
        with.onConsume(shooter)
    }

    /**
     * ダメージポーションが投げられたことを表します。
     * この際、手段として消費されますが、剥奪はされません。
     *
     * @param shooter 投げた参加者
     * @param with ポーション
     */
    fun throwPotion(shooter: Participant, with: DamagePotionEffect) {
        with.onConsume(shooter)
    }
}