package com.github.tanokun.bakajinrou.plugin.participant.ability

import com.github.tanokun.bakajinrou.api.participant.Participant
import org.bukkit.entity.Player

/**
 * 対象を選択して発動するタイプの能力を表します。
 */
interface UsableAbility {
    /**
     * `「○○」[action]`に続く形になるような単語が来ます。
     * 主に、GUIの表示で使われます。
     *
     * 例: `「○○」を霊媒する`
     */
    val action: String

    /**
     * 指定されたプレイヤーに対して能力を発動します。
     *
     * この関数は副作用を伴う場合が多く、
     * 発動処理の本質的なロジックを担当します。
     *
     * @param target 能力の対象となる参加者
     * @param user 能力使用者
     */
    fun useOn(target: Participant, user: Player)
}