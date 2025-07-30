package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class GrowingNotifier(
    private val jinrouGame: JinrouGame,
    private val getBukkitPlayer: (Participant) -> Player?
) {
    /**
     * 残り時間 [glowingStartMinutes] 分の発光お知らせを全ての参加者に表示します。
     *
     * @param glowingStartMinutes 何分から発光が始まるか。
     */
    fun announceGlowingStart(glowingStartMinutes: Int) {
        jinrouGame.participants.forEach {
            val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

            bukkitPlayer.sendMessage(
                Component.text("[人狼] 残り${glowingStartMinutes}分間から、定期発光が始まります。")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.color(0x00FF00))
            )
        }
    }

    /**
     * 以下の役職を除き、5秒間の発光を付与します。
     * - 人狼
     * - 妖狐
     */
    fun growCitizens() {
        jinrouGame.participants
            .filterNot { it.isPosition<WolfPosition>() || it.isPosition<FoxPosition>() }
            .forEach {
                val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

                val growingEffect = PotionEffect(PotionEffectType.GLOWING, 100, 1, false, false)
                bukkitPlayer.addPotionEffect(growingEffect)
            }
    }
}