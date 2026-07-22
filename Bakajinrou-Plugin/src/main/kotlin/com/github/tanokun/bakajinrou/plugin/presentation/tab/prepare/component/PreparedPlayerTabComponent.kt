package com.github.tanokun.bakajinrou.plugin.presentation.tab.prepare.component

import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import com.github.tanokun.bakajinrou.plugin.presentation.tab.DummyUUID
import com.github.tanokun.bakajinrou.plugin.presentation.tab.TabEntryComponent
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class PreparedPlayerTabComponent(
    override val dummyUuid: DummyUUID,
    target: Player,
    category: PreparedPlayerCategory,
    colorPallet: ColorPallet,
): TabEntryComponent {
    private val entry = run {
        val prefixColor = when (category) {
            PreparedPlayerCategory.PARTICIPANT -> NamedTextColor.GRAY
            PreparedPlayerCategory.SPECTATOR -> colorPallet.getColor("spectator")
        }
        val displayName = Component.text()
            .append(Component.text(category.prefix, prefixColor, TextDecoration.BOLD))
            .append(Component.space())
            .append(Component.text(target.name, NamedTextColor.GRAY))
            .build()

        ClientboundPlayerInfoUpdatePacket.Entry(
            dummyUuid.uuid,
            (target as CraftPlayer).profile,
            true,
            target.ping,
            GameType.byId(target.gameMode.value),
            PaperAdventure.asVanilla(displayName),
            true,
            category.listOrder,
            null,
        )
    }

    override fun toPacketEntry(viewer: Player) = entry
}

enum class PreparedPlayerCategory(
    val prefix: String,
    val listOrder: Int,
) {
    PARTICIPANT("[参加者]", 0),
    SPECTATOR("[観戦者]", -100),
}
