package com.github.tanokun.bakajinrou.plugin.interaction.method.ability.gui

import com.github.tanokun.bakajinrou.api.participant.ParticipantId
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.cache.PlayerNameCache
import com.github.tanokun.bakajinrou.plugin.common.cache.PlayerSkinCache
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.GameKeys
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.SkullBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class UsableAbilityButton(
    private val translator: JinrouTranslator,
    private val target: ParticipantId,
    private val description: GameKeys.Gui.Using,
    private val onClick: (clicker: ParticipantId, target: ParticipantId) -> Unit
) : AbstractItem() {
    override fun getItemProvider(player: Player): ItemProvider {
        val name = PlayerNameCache.get(target.uniqueId) ?: "unknownPlayer"
        val texture = PlayerSkinCache.getTexture(target.uniqueId) ?: ""

        val displayName = translator.translate(description, player.locale(), Component.text(name))

        return SkullBuilder(SkullBuilder.HeadTexture(texture))
            .setDisplayName(AdventureComponentWrapper(displayName))
            .setItemFlags(ItemFlag.entries)

    }

    override fun handleClick(clickType: ClickType, clicker: Player, event: InventoryClickEvent) {
        clicker.playSound(Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f))

        onClick(clicker.uniqueId.asParticipantId(), target)

        clicker.closeInventory()
    }
}
