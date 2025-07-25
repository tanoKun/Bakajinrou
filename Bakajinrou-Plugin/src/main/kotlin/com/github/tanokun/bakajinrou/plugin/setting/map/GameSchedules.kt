package com.github.tanokun.bakajinrou.plugin.setting.map

import com.github.tanokun.bakajinrou.api.JinrouGame
import com.github.tanokun.bakajinrou.api.ParticipantStates
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.api.participant.position.wolf.WolfPosition
import com.github.tanokun.bakajinrou.plugin.cache.BukkitPlayerNameCache
import com.github.tanokun.bakajinrou.plugin.formatter.ParticipantsFormatter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration.Companion.seconds

class GameSchedules(
    private val jinrouGame: JinrouGame,
) {
    /**
     * "残り時間: ${minutes}分 ${seconds}秒" のフォーマットで、残り時間をアクションバーに表示します。
     *
     * @param leftSeconds 残り時間
     */
    fun showLeftTime(leftSeconds: Long) {
        if (leftSeconds < 0) throw IllegalArgumentException("残り時間は0以上である必要があります。")

        val formattedTime = leftSeconds.seconds.toComponents { _, minutes, seconds, _ ->
            "残り時間: ${minutes}分 ${seconds}秒"
        }

        jinrouGame.participants.forEach {
            val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

            bukkitPlayer.sendActionBar(
                Component.text(formattedTime)
                    .color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD)
            )
        }
    }

    /**
     * 全ての生存状態の参加者に、クオーツを配布します。
     */
    fun giveQuartzToSurvivedParticipants() {
        jinrouGame.participants
            .filter { it.state == ParticipantStates.SURVIVED }
            .forEach {
            val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

            bukkitPlayer.inventory.addItem(ItemStack(Material.QUARTZ))
        }
    }

    /**
     * 残り時間3分の発光お知らせを全ての参加者に表示します。
     *
     * @see growCitizens
     */
    fun notifyParticipantsOfGrowing() {
        jinrouGame.participants.forEach {
            val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

            bukkitPlayer.sendMessage(
                Component.text("[人狼] 残り3分間から、定期発光が始まります。")
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

                val growingEffect = PotionEffect(PotionEffectType.GLOWING, 5, 1, false, false)
                bukkitPlayer.addPotionEffect(growingEffect)
        }
    }

    /**
     * 以下の役職の付与者一覧を、全ての参加者に表示します。
     * - 人狼
     * - 妖狐
     *
     * @param bukkitPlayerNameCache プレイヤー名のキャッシュ
     *
     * @see com.github.tanokun.bakajinrou.plugin.formatter.ParticipantsFormatter
     */
    fun notifyWolfsAndFox(bukkitPlayerNameCache: BukkitPlayerNameCache) {
        val formatter =
            ParticipantsFormatter(jinrouGame.participants, bukkitPlayerNameCache) { Bukkit.getPlayer(it.uniqueId) }

        jinrouGame.participants.forEach {
            val bukkitPlayer = getBukkitPlayer(it) ?: return@forEach

            bukkitPlayer.sendMessage(formatter.formatWolf())
            bukkitPlayer.sendMessage(formatter.formatFox())
        }
    }

    private fun getBukkitPlayer(participant: Participant) = Bukkit.getPlayer(participant.uniqueId)
}