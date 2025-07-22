package com.github.tanokun.bakajinrou.plugin.protection

import com.github.tanokun.bakajinrou.api.participant.protection.Protection
import com.github.tanokun.bakajinrou.api.participant.protection.ProtectionResult
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.util.*

class PlayerProtection(private val uniqueId: UUID): Protection {
    /**
     * 攻撃の無効化ができる要因を特定します。
     * 複数の要因がある場合、以下の順番で優先されます。
     * - 盾
     * - ポーション
     * - トーテム
     *
     * @return 無効化要因
     */
    override fun hasProtection(): ProtectionResult {
        val player = Bukkit.getPlayer(uniqueId) ?: return ProtectionResult.NONE

        if (isActiveShield(player)) return ProtectionResult.SHIELD
        if (hasResistance(player)) return ProtectionResult.POTION_RESISTANCE
        if (hasTotemInHand(player)) return ProtectionResult.TOTEM

        return ProtectionResult.NONE
    }

    private fun hasTotemInHand(player: Player): Boolean {
        if (player.inventory.itemInMainHand.type == Material.TOTEM_OF_UNDYING) return true
        if (player.inventory.itemInOffHand.type == Material.TOTEM_OF_UNDYING) return true

        return false
    }

    private fun isActiveShield(player: Player): Boolean {
        return player.activeItem.type == Material.SHIELD
    }

    private fun hasResistance(player: Player): Boolean {
        return player.hasPotionEffect(PotionEffectType.RESISTANCE)
    }
}