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


    fun joinEngine(type: HandlerType, player: Player) {
        val engine = engines[type] ?: throw IllegalArgumentException("Unknown engine type: $type")
        val renderer = renderers[player] ?: throw IllegalArgumentException("Unknown renderer for $player")

        eachEngines.put(player, engine)?.renderRemoval()

        engine.registerRenderer(renderer)
        engine.applyInitialization()
    }

    fun quitEngine(player: Player) {
        eachEngines.remove(player)
    }

    fun createRenderer(player: Player) {
        renderers[player] = TabRenderer(authenticator, player)
    }

    fun deleteRenderer(player: Player) {
        val renderer = renderers.remove(player)
        val engine = eachEngines.remove(player)

        if (renderer != null && engine != null) engine.unregisterRenderer(renderer)
    }

    fun editEngine(type: HandlerType, block: TabEngineHandlerDsl.() -> Unit) {
        val engine = engines[type] ?: throw IllegalArgumentException("Unknown engine type: $type")

        engine.apply {
            block(TabEngineHandlerDsl(this))
            applyDifferences()
        }
    }
}