package com.github.tanokun.bakajinrou.plugin.gui.ability.fortune

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.gui.ability.UsableAbility
import com.github.tanokun.bakajinrou.plugin.position.fox.FoxThirdPosition
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
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
     * これは、任意で決めた [positionPrefix] の元、実行することが多いです。
     *
     * @param targetName 対象プレイヤーの名前
     * @param positionPrefix 公開される役職名
     * @param color 表示に使用する色
     * @param user 占いを行ったプレイヤー
     */
    fun showResult(targetName: String, positionPrefix: String, color: TextColor, user: Player) {
        user.sendMessage(
            component {
                text("占いの結果: 「") color gray deco bold
                text(targetName) color white deco bold
                text("」は、「") color gray deco bold
                text(positionPrefix) color color.asHexString() deco bold
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