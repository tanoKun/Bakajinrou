package com.github.tanokun.bakajinrou.plugin.rendering.tab.handler

import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabEngine
import com.github.tanokun.bakajinrou.plugin.rendering.tab.TabRenderer
import com.github.tanokun.bakajinrou.plugin.rendering.tab.authentication.TabAuthenticator
import org.bukkit.entity.Player
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class TabHandler(
    private val authenticator: TabAuthenticator
) {
    private val renderers = hashMapOf<Player, TabRenderer>()

    private val eachHandlerTypes = hashMapOf<Player, TabHandlerType>()

    private val engines = hashMapOf<TabHandlerType, TabEngine>()

    /**
     * 指定した種類のエンジンを作成します。
     *
     * @param type 作成するエンジンの種類
     *
     * @throws IllegalArgumentException 指定した種類のエンジンが既に存在する場合
     */
    fun createEngine(type: TabHandlerType) {
        if (engines.containsKey(type)) throw IllegalArgumentException("Engine $type already exists")

        engines[type] = TabEngine()
    }

    /**
     * 指定した種類のエンジンを削除します。
     *
     * @param type 削除するエンジンの種類
     *
     * @throws IllegalArgumentException 指定した種類のエンジンが存在しない場合
     */
    fun deleteEngine(type: TabHandlerType) {
        if (!engines.containsKey(type)) throw IllegalArgumentException("Engine $type is not found")

        val engine = engines.remove(type)

        eachHandlerTypes
            .filter { (_, eachType) -> eachType == type }
            .forEach { (player, _) -> quitEngine(player) }

        engine?.unregisterAll()
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
    fun joinEngine(type: TabHandlerType, player: Player) {
        val engine = engines[type] ?: throw IllegalArgumentException("Unknown engine type: $type")
        val renderer = renderers[player] ?: throw IllegalArgumentException("Unknown renderer for $player")

        quitEngine(player)

        eachHandlerTypes[player] = type

        engine.registerRenderer(renderer)
    }

    private fun quitEngine(player: Player) {
        val previousType = eachHandlerTypes.remove(player)
        val previousEngine = engines[previousType]
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


    fun editEngine(type: TabHandlerType, block: TabEngineHandlerDsl.() -> Unit) {
        val engine = engines[type] ?: throw IllegalArgumentException("Unknown engine type: $type")

        engine.apply {
            block(TabEngineHandlerDsl(this))
            applyDifferences()
        }
    }
}