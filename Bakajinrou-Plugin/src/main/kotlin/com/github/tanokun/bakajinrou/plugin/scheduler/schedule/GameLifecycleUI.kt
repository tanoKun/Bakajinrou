/*
package com.github.tanokun.bakajinrou.plugin.scheduler.schedule

import com.github.tanokun.bakajinrou.api.map.GameMap
import com.github.tanokun.bakajinrou.api.map.PointLocation
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.formatter.display.updatePlayerListName
import com.github.tanokun.bakajinrou.plugin.formatter.toTick
import com.github.tanokun.bakajinrou.plugin.method.appearance.BowItem
import com.github.tanokun.bakajinrou.plugin.method.weapon.AttackByArrow
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.darkRed
import plutoproject.adventurekt.text.text
import kotlin.time.Duration.Companion.seconds

class GameLifecycleUI(
    private val playerProvider: BukkitPlayerProvider
) {
    */
/**
     * ゲームの開始時に呼び出します。
     *
     * 副作用:
     * - 各参加者をマップのスポーン地点にテレポート
     * - タイトルを表示し、スタート演出
     * - 移動速度上昇 と 透明化 のポーション効果を10秒間に付与
     * - 透明化解除後、Bow と Arrow の手段の追加
     *
     * @param participants ゲームの全ての参加者
     * @param controller ゲームを制御するコントローラ。コルーチンのスコープも含む
     * @param gameMap 使用されるマップ
     *//*

     fun startingGame(participants: ParticipantScope.All, controller: JinrouGameController, gameMap: GameMap) {
        participants.forEach {
            val bukkitPlayer: Player = playerProvider.get(it) ?: return@forEach

            bukkitPlayer.teleport(gameMap.spawnPoint.asBukkit())
            bukkitPlayer.setArrowsInBody(0, false)
            bukkitPlayer.gameMode = GameMode.ADVENTURE

            val startTitle = Title.title(
                component {
                    text("バカ人狼") color darkRed deco bold
                },

                component {
                    text("～ スタート ～") color darkRed deco bold
                }
            )

            bukkitPlayer.showTitle(startTitle)
            bukkitPlayer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 10.seconds.toTick(), 2, true, false))
            bukkitPlayer.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 10.seconds.toTick(), 1, true, false))
            controller.mainDispatcherScope.launch {
                delay(10.seconds)
                it.grantMethod(BowItem())
                it.grantMethod(AttackByArrow(controller))
            }

            bukkitPlayer.updatePlayerListName()
        }

        gameMap.spawnPoint.asBukkit().world.playSound(
            Sound.sound(NamespacedKey("minecraft", "entity.wolf.howl"), Sound.Source.PLAYER, 1.0f, 1.0f)
        )
    }

    */
/**
     * ゲーム終了時に呼び出します。
     *
     * 副作用:
     * - 各参加者をロビー地点へテレポート
     * - 参加者のインベントリをクリア
     * - タブリストの名前表示を更新
     *
     * @param participants ゲームの全ての参加者
     * @param gameMap 使用されていたマップ。ロビー地点を含む。
     *//*

     fun finishGame(participants: ParticipantScope.All, gameMap: GameMap) {
        participants.forEach {
            val bukkitPlayer: Player = playerProvider.get(it) ?: return@forEach

            bukkitPlayer.teleport(gameMap.lobbyPoint.asBukkit())
            bukkitPlayer.inventory.clear()
            bukkitPlayer.updatePlayerListName()
        }
    }

    private fun PointLocation.asBukkit(): Location {
        val world = Bukkit.getWorld(this.worldName) ?: Bukkit.getWorlds().first()

        return Location(world, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
    }
}*/
