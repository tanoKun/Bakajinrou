package com.github.tanokun.bakajinrou.plugin.method.protective

import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.protect.method.effect.ResistanceEffect
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.potion.PotionEffectType
import java.util.*

class ResistanceProtectiveEffect: ResistanceEffect() {
    override val uniqueId: UUID = UUID.randomUUID()

    override fun isActive(of: Participant): Boolean = true

    override fun onConsume(consumer: Participant) {
        Bukkit.getPlayer(consumer.uniqueId)?.apply {
            removePotionEffect(PotionEffectType.RESISTANCE)
            world.playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 1.0f, 1.0f)
        }
    }
}