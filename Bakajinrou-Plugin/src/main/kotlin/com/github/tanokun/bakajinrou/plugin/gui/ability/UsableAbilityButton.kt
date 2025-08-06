/*
package com.github.tanokun.bakajinrou.plugin.gui.ability

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.cache.PlayerSkinCache
import com.github.tanokun.bakajinrou.plugin.participant.BukkitPlayerProvider
import com.github.tanokun.bakajinrou.plugin.participant.ability.UsableAbility
import net.kyori.adventure.sound.Sound
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.gray
import plutoproject.adventurekt.text.style.white
import plutoproject.adventurekt.text.text
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.SkullBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class UsableAbilityButton(
    private val candidate: Participant,
    private val usableAbility: UsableAbility,
    private val user: Participant,
    private val method: GrantedMethod
): AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        val name = BukkitPlayerProvider.get(candidate)?.name ?: PlayerNameCache.get(candidate) ?: "unknownPlayer"
        val texture = PlayerSkinCache.getTexture(candidate.uniqueId) ?: ""

        val displayName = component {
            text("「") color gray deco bold
            text(name) color white deco bold
            text("」${usableAbility.action}") color gray deco bold
        }

        return SkullBuilder(SkullBuilder.HeadTexture(texture))
            .setDisplayName(AdventureComponentWrapper(displayName))
            .setItemFlags(ItemFlag.entries)

    }

    override fun handleClick(clickType: ClickType, clicker: Player, event: InventoryClickEvent) {
        clicker.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))

        usableAbility.useOn(candidate, clicker)
        user.removeMethod(method)

        clicker.closeInventory()
    }
}*/
