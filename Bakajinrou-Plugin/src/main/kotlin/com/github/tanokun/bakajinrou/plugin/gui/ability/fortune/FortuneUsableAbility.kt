package com.github.tanokun.bakajinrou.plugin.gui.ability.fortune

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.gui.ability.AbilityResult
import com.github.tanokun.bakajinrou.plugin.gui.ability.UsableAbility
import com.github.tanokun.bakajinrou.plugin.participant.position.fox.FoxThirdPosition
import org.bukkit.Bukkit
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
 * 結果表示や、狐の特殊効果など、占い能力全てに共通するロジックを提供します。
 */
abstract class FortuneUsableAbility: UsableAbility {
    override val action: String = "を占う"

    /**
     * 占い結果を、指定したプレイヤーに送信します。
     *
     * @param targetName 対象プレイヤーの名前
     * @param abilityResult 占いの結果
     * @param user 占いを行ったプレイヤー
     */
    fun showResult(targetName: String, abilityResult: AbilityResult, user: Player) {
        user.sendMessage(
            component {
                text("占いの結果: 「") color gray deco bold
                text(targetName) color white deco bold
                text("」は、「") color gray deco bold
                raw { abilityResult.result } deco bold
                text("」です。") color gray deco bold
            }
        )
    }

    /**
     * 対象が狐だった場合、光るエフェクトを適用します。
     *
     * @param target 占い対象の参加者
     */
    fun glowingFox(target: Participant) {
        val position = (target.position as? FoxThirdPosition) ?: return
        val foxPlayer = Bukkit.getPlayer(target.uniqueId) ?: return

        position.onFortune(foxPlayer)
    }
}