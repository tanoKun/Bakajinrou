package com.github.tanokun.bakajinrou.plugin.interaction.participant.comingout.gui

import com.github.tanokun.bakajinrou.api.participant.prefix.ComingOut
import com.github.tanokun.bakajinrou.game.participant.comingout.ComingOutHandler
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Material
import org.bukkit.entity.Player
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window


/**
 * プレイヤーがカミングアウトを行えるインターフェースを提供します。
 */
class ComingOutGUI(
    private val translator: JinrouTranslator,
    handler: ComingOutHandler,
    mainScope: CoroutineScope
) {
    private val lastWolfButton = ComingOutButton(translator, ComingOut.LAST_WOLF, handler, Material.REDSTONE_BLOCK, mainScope)
    private val fortuneButton = ComingOutButton(translator, ComingOut.FORTUNE, handler, Material.ENCHANTED_BOOK, mainScope)
    private val mediumButton = ComingOutButton(translator, ComingOut.MEDIUM, handler, Material.HEART_OF_THE_SEA, mainScope)
    private val knightButton = ComingOutButton(translator, ComingOut.KNIGHT, handler, Material.TOTEM_OF_UNDYING, mainScope)
    private val cancelButton = CancelComingOutButton(translator, handler, mainScope)

    private val gui: Gui = Gui.normal()
        .setStructure("# # l f m k c # #")
        .addIngredient('l', lastWolfButton)
        .addIngredient('f', fortuneButton)
        .addIngredient('m', mediumButton)
        .addIngredient('k', knightButton)
        .addIngredient('c', cancelButton)
        .build()

    fun open(player: Player) {
        Window.single()
            .setGui(gui)
            .setTitle(AdventureComponentWrapper(translator.translate(GameKeys.ComingOut.Gui.TITLE, player.locale())))
            .setViewer(player)
            .build()
            .open()
    }
}
