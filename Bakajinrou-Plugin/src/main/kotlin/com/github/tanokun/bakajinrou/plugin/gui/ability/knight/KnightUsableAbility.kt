package com.github.tanokun.bakajinrou.plugin.gui.ability.knight

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.plugin.gui.ability.UsableAbility
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text

abstract class KnightUsableAbility: UsableAbility {
    override val action: String = "を加護する"

    /**
     * 加護の結果を、指定したプレイヤーに送信します。

     * @param targetName 対象プレイヤーの名前
     * @param user 加護を行ったプレイヤー
     */
    fun showResult(targetName: String, user: Player) {
        user.sendMessage(
            component {
                text("加護の結果: 「") color gray deco bold
                text(targetName) color white deco bold
                text("」に加護を付与しました。") color gray deco bold
            }
        )
    }

    fun grantMethod(target: Participant, method: GrantedMethod) {
        target.grantMethod(method)

        Bukkit.getPlayer(target.uniqueId)?.sendMessage(
            component {
                text("加護の結果: 「") color gray deco bold
                text("???") color white deco bold
                text("」から加護を付与されました。") color gray deco bold
            }
        )
    }
}