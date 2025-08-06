package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.isFox
import com.github.tanokun.bakajinrou.api.participant.isWolf
import com.github.tanokun.bakajinrou.api.participant.or
import com.github.tanokun.bakajinrou.plugin.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration.Companion.seconds

class GlowingNotifier(
    private val playerProvider: BukkitPlayerProvider
) {
    /**
     * 残り時間 [glowingStartMinutes] 分の発光お知らせを全ての参加者に表示します。
     *
     * @param participants ゲームの全ての参加者
     * @param glowingStartMinutes 何分から発光が始まるか。
     */
     fun announceGlowingStart(participants: ParticipantScope.All, glowingStartMinutes: Int) {
        participants.forEach {
            val bukkitPlayer = playerProvider.get(it) ?: return@forEach

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
     *
     * @param participants ゲームの全ての参加者
     */
     fun glowCitizens(participants: ParticipantScope.All) {
        participants
            .excludes(::isWolf or ::isFox)
            .forEach {
                val bukkitPlayer = playerProvider.get(it) ?: return@forEach

                val glowingEffect = PotionEffect(PotionEffectType.GLOWING, 5.seconds.toTick(), 1, false, false)
                bukkitPlayer.addPotionEffect(glowingEffect)
            }
    }
}