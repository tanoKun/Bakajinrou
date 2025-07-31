package com.github.tanokun.bakajinrou.plugin.gui.ability.medium

import com.github.tanokun.bakajinrou.plugin.gui.ability.UsableAbility
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
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
     * これは、任意で決めた [positionPrefix] の元、実行することが多いです。
     *
     * @param targetName 対象プレイヤーの名前
     * @param positionPrefix 公開される役職名
     * @param color 表示に使用する色
     * @param user 霊媒を行ったプレイヤー
     */
    fun showResult(targetName: String, positionPrefix: String, color: TextColor, user: Player) {
        user.sendMessage(
            component {
                text("霊媒の結果: 「") color gray deco bold
                text(targetName) color white deco bold
                text("」は、「") color gray deco bold
                text(positionPrefix) color color.asHexString() deco bold
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