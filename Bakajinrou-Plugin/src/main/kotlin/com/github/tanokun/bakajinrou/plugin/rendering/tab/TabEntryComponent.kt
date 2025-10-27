package com.github.tanokun.bakajinrou.plugin.rendering.tab

import com.mojang.authlib.GameProfile
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.craftbukkit.entity.CraftPlayer

interface TabEntryComponent {
    val dummyUuid: DummyUUID

    /**
     * インスタンス作成時に決定され、変更されない値のみを返します。
     * 
     * @return 決定されたエントリー
     */
    fun toPacketEntry(): ClientboundPlayerInfoUpdatePacket.Entry

    fun createGameProfile(target: CraftPlayer): GameProfile {
        return target.profile
    }
}