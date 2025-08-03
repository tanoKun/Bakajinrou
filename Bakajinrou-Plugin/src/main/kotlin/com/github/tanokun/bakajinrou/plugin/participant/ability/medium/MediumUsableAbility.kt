package com.github.tanokun.bakajinrou.plugin.participant.ability.medium

import com.github.tanokun.bakajinrou.plugin.participant.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.participant.ability.UsableAbility
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text

/**
 * 結果表示や、狐の特殊効果など、霊媒能力全てに共通するロジックを提供します。
 */
abstract class MediumUsableAbility: UsableAbility {
    override val action: String = "を霊媒する"

    /**
     * 霊媒の結果を、指定したプレイヤーに送信します。
     *
     * @param targetName 対象プレイヤーの名前
     * @param abilityResult 霊媒の結果
     * @param user 霊媒を行ったプレイヤー
     */
    fun showResult(targetName: String, abilityResult: AbilityResult, user: Player) {
        user.sendMessage(
            component {
                text("霊媒の結果: 「") color gray deco bold
                text(targetName) color white deco bold
                text("」は、「") color gray deco bold
                raw { abilityResult.result } deco bold
                text("」です。") color gray deco bold
            }
        )
    }

    /**
     * 死亡していない参加者を霊媒したときの結果を、指定したプレイヤーに送信します。
     *
     * @param targetName 対象プレイヤーの名前
     * @param user 霊媒を行ったプレイヤー
     */
    fun showResultNotDead(targetName: String, user: Player) {
        user.sendMessage(
            component {
                text("霊媒の結果: 「") color gray deco bold
                text(targetName) color white deco bold
                text("」はまだ生存しているため、霊媒できませんでした。") color gray deco bold
            }
        )
    }
}