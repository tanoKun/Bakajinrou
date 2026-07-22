package com.github.tanokun.bakajinrou.plugin.presentation.tab.gaming.component

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.state.GameStore
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.presentation.tab.DummyUUID
import io.papermc.paper.adventure.PaperAdventure
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.world.level.GameType
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import kotlin.math.abs

/** 死亡参加者と外部観戦者が共有する、全役職開示の表示です。 */
open class SharedInfoByObserverComponent(
    override val dummyUuid: DummyUUID,
    private val game: GameStore,
    target: Player,
    translator: JinrouTranslator,
): ParticipantInfoInGameComponent(translator) {
    private val targetId = target.uniqueId.asParticipantId()
    private val gameProfile = (target as CraftPlayer).profile
    private val latency = target.ping

    override fun orderDecider(target: Participant): Int {
        val hashCode = abs(target.position.hashCode())
        return if (target.isDead()) -hashCode else hashCode
    }

    override fun toPacketEntry(viewer: Player): ClientboundPlayerInfoUpdatePacket.Entry {
        val targetParticipant = game.getParticipant(targetId)
            ?: throw IllegalStateException("Target is not a participant")

        val gameType = if (targetParticipant.isDead()) GameType.SPECTATOR else GameType.SURVIVAL
        val displayName = createDisplayName(
            targetParticipant,
            targetParticipant,
            gameProfile.name,
            viewer.locale(),
        )

        return ClientboundPlayerInfoUpdatePacket.Entry(
            dummyUuid.uuid,
            gameProfile,
            true,
            latency,
            gameType,
            PaperAdventure.asVanilla(displayName),
            true,
            orderDecider(targetParticipant),
            null,
        )
    }
}
