package com.github.tanokun.bakajinrou.plugin.interaction.player.setting.gui

import com.github.tanokun.bakajinrou.plugin.common.setting.GameSettings
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import kotlinx.coroutines.*
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.raw
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem
import java.util.*

private val lore = listOf(
    component {
        text("「左クリック」") color white deco bold
        text("で、予約数を") color gray
        text("増加") color gray deco bold
        text("させます。") color gray
    },

    component {
        text("「右クリック」") color white deco bold
        text("で、予約数を") color gray
        text("減少") color gray deco bold
        text("させます。") color gray
    }
).map { AdventureComponentWrapper(it) }

class ChangePositionAmountButton(
    private val position: RequestedPositions,
    private val settings: GameSettings,
    private val material: Material,
    private val translator: JinrouTranslator,
): AbstractItem() {
    private val cooltime = hashSetOf<UUID>()

    private val cooltimeScope = CoroutineScope(SupervisorJob() + CoroutineExceptionHandler { context, throwable -> throwable.printStackTrace() })

    override fun getItemProvider(player: Player): ItemProvider {
        val amount = settings.getAmountBy(position)

        val displayName = component {
            text("「") color gray deco bold
            raw { translator.translate(position.formatKey, player.locale()) } deco bold
            text("」→ ") color gray deco bold
            text("${settings.getAmountBy(position)}人") color white deco bold
        }

        val builder = if (amount <= 0)
            ItemBuilder(ItemStack(Material.FLOW_BANNER_PATTERN)).setItemFlags(ItemFlag.entries)
         else
            ItemBuilder(ItemStack(material)).setAmount(amount)

        return builder
            .setDisplayName(AdventureComponentWrapper(displayName))
            .setLore(lore)

    }

    override fun handleClick(clickType: ClickType, clicker: Player, event: InventoryClickEvent) {
        if (cooltime.contains(clicker.uniqueId)) return

        cooltimeScope.launch {
            cooltime.add(clicker.uniqueId)
            delay(100)
            cooltime.remove(clicker.uniqueId)
        }

        if (clickType == ClickType.LEFT) {
            settings.increase(position)
            sendChangeMessage()
            clicker.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
            notifyWindows()
            return
        }

        if (clickType == ClickType.RIGHT) {
            if (settings.getAmountBy(position) <= 0) {
                clicker.sendMessage(component {
                    text("予約数を0人未満にすることはできません。") color gray deco bold
                })

                clicker.playSound(Sound.sound(NamespacedKey("minecraft", "block.note_block.bass"), Sound.Source.PLAYER, 3.0f, 1.0f))
                return
            }

            settings.decrease(position)
            sendChangeMessage()
            clicker.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))
            notifyWindows()
        }
    }

    private fun sendChangeMessage() {
        Bukkit.getOnlinePlayers().forEach { player ->
            player.sendMessage(component {
                text("「") color gray deco bold
                raw { translator.translate(position.formatKey, player.locale()) } deco bold
                text("」の予約数を${settings.getAmountBy(position)}人にしました。") color gray deco bold
            })
        }
    }
}
