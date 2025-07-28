package com.github.tanokun.bakajinrou.plugin.method

import com.github.tanokun.bakajinrou.game.controller.JinrouGameController
import com.github.tanokun.bakajinrou.plugin.method.optional.InvisibilityPotionItem
import com.github.tanokun.bakajinrou.plugin.method.optional.ResistancePotionItem
import com.github.tanokun.bakajinrou.plugin.method.optional.SpeedUpPotionItem
import com.github.tanokun.bakajinrou.plugin.method.protective.ShieldProtectiveItem
import com.github.tanokun.bakajinrou.plugin.method.weapon.AttackByDamagePotionEffect
import com.github.tanokun.bakajinrou.plugin.method.weapon.AttackBySwordItem
import org.bukkit.plugin.Plugin
import kotlin.random.Random

class BukkitItemFactory(plugin: Plugin, jinrouGameController: JinrouGameController) {
    private val randomItems = listOf<() -> AsBukkitItem>(
        { AttackBySwordItem(jinrouGameController) },
        { AttackByDamagePotionEffect(jinrouGameController) },
        { InvisibilityPotionItem() },
        { SpeedUpPotionItem() },
        { ResistancePotionItem(plugin) },
        { ShieldProtectiveItem() },
    )

    fun random(random: Random = Random): AsBukkitItem = randomItems.random(random).invoke()
}