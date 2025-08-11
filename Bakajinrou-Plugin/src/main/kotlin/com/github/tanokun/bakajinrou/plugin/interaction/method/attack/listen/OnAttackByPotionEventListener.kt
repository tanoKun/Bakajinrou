package com.github.tanokun.bakajinrou.plugin.interaction.method.attack.listen

import com.github.tanokun.bakajinrou.api.attack.method.DamagePotionMethod
import com.github.tanokun.bakajinrou.api.participant.asParticipantId
import com.github.tanokun.bakajinrou.game.attack.Attacking
import com.github.tanokun.bakajinrou.plugin.common.listener.LifecycleEventListener
import com.github.tanokun.bakajinrou.plugin.interaction.method.adapter.ItemPersistent.getMethodId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionType

class OnAttackByPotionEventListener(
    plugin: Plugin, attacking: Attacking, mainScope: CoroutineScope
): LifecycleEventListener(plugin, {
    register<PotionSplashEvent> { event ->
        val potion = event.entity

        val attacker = potion.shooter as? Player ?: return@register

        if (event.potion.potionMeta.basePotionType != PotionType.HARMING) return@register

        val victims = event.affectedEntities
            .filterIsInstance<Player>()
            .map { it.uniqueId.asParticipantId() }

        val attackMethod = potion.item.getMethodId() ?: return@register

        mainScope.launch {
            attacking.attack<DamagePotionMethod>(by = attacker.uniqueId.asParticipantId(), victims = victims, attackMethod)
        }
    }
})
