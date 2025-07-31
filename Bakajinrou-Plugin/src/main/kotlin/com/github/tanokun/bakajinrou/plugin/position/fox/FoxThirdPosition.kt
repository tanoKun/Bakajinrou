package com.github.tanokun.bakajinrou.plugin.position.fox

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.ParticipantScope
import com.github.tanokun.bakajinrou.api.participant.position.Position
import com.github.tanokun.bakajinrou.api.participant.position.Prefix
import com.github.tanokun.bakajinrou.api.participant.position.fox.FoxPosition
import com.github.tanokun.bakajinrou.plugin.formatter.toTick
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.title.Title
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.bold
import plutoproject.adventurekt.text.style.darkRed
import plutoproject.adventurekt.text.text
import kotlin.time.Duration.Companion.seconds

object FoxThirdPosition: FoxPosition {
    override val prefix: Prefix = Prefix(revealedPrefix = "妖狐", defaultPrefix = "妖狐")

    override val publicPosition: Position = this

    override fun doAtStarting(self: Participant, participants: ParticipantScope.All) {}

    fun onFortune(player: Player) {
        player.playSound(Sound.sound(NamespacedKey("minecraft", "entity.lightning_bolt.thunder"), Sound.Source.PLAYER, 1.0f, 1.0f))

        val message = component {
            text("占われてしまった!") color darkRed deco bold
        }

        val glowingEffect = PotionEffect(PotionEffectType.GLOWING, 15.seconds.toTick(), 1, false, false)
        player.addPotionEffect(glowingEffect)

        player.sendMessage(message)
        player.showTitle(
            Title.title(
                component {  },
                message
            )
        )
    }
}