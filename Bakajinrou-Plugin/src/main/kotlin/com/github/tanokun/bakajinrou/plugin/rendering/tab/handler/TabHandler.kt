package com.github.tanokun.bakajinrou.plugin.rendering.tab.handler

import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEngine
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabRenderer
import com.github.tanokun.bakajinrou.plugin.rendering.tab.authentication.TabAuthenticator
import org.bukkit.entity.Player
import org.koin.core.annotation.Single
import java.util.*

@Single(createdAtStart = true)
class TabHandler(
    private val authenticator: TabAuthenticator
) {
    private val renderers = hashMapOf<Player, TabRenderer>()

    private val eachEngines = hashMapOf<Player, TabEngine>()

    private val engines = EnumMap<HandlerType, TabEngine>(HandlerType::class.java).apply {
        HandlerType.entries.forEach { put(it, TabEngine()) }
    }

    /**
     * 指定したプレイヤーを、指定した種類のエンジンに参加させます。
     * 既に参加しているエンジンがある場合、それを退出してから参加します。
     *
     * @param type 参加させるエンジンの種類
     * @param player 参加させるプレイヤー
     *
     * @throws IllegalArgumentException 指定した種類のエンジンが存在しない場合、または指定したプレイヤーのレンダラーが存在しない場合
     */
    fun joinEngine(type: HandlerType, player: Player) {
        val engine = engines[type] ?: throw IllegalArgumentException("Unknown engine type: $type")
        val renderer = renderers[player] ?: throw IllegalArgumentException("Unknown renderer for $player")

        quitEngine(player)

        eachEngines[player] = engine

        engine.registerRenderer(renderer)
    }

    /**
     * 指定したプレイヤーを、参加しているエンジンから退出させます。
     * 参加しているエンジンがない場合、何も起こりません。
     *
     * @param player 退出させるプレイヤー
     */
    fun quitEngine(player: Player) {
        val previousEngine = eachEngines.remove(player)
        val renderer = renderers[player]

        if (renderer != null && previousEngine != null) previousEngine.unregisterRenderer(renderer)
    }

    /**
     * 指定したプレイヤーのレンダラーを作成します。
     *
     * @param player レンダラーを作成するプレイヤー
     *
     * @throws IllegalArgumentException 指定したプレイヤーのレンダラーが既に存在する場合
     */
    fun createRenderer(player: Player) {
        if (renderers.containsKey(player)) throw IllegalArgumentException("It already has a renderer for $player")

        renderers[player] = TabRenderer(authenticator, player)
    }

    /**
     * 指定したプレイヤーのレンダラーを削除します。
     * 参加しているエンジンがある場合、それからも退出させます。
     *
     * @param player レンダラーを削除するプレイヤー
     */
    fun deleteRenderer(player: Player) {
        quitEngine(player)

        renderers.remove(player)
    }

    fun editEngine(type: HandlerType, block: TabEngineHandlerDsl.() -> Unit) {
        val engine = engines[type] ?: throw IllegalArgumentException("Unknown engine type: $type")

        engine.apply {
            block(TabEngineHandlerDsl(this))
            applyDifferences()
        }
    }
}