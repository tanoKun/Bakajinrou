package com.github.tanokun.bakajinrou.plugin.participant

import com.github.tanokun.bakajinrou.api.method.GrantedMethod
import com.github.tanokun.bakajinrou.api.method.ProtectiveMethod
import com.github.tanokun.bakajinrou.api.participant.GrantedStrategy
import com.github.tanokun.bakajinrou.api.participant.Participant
import com.github.tanokun.bakajinrou.api.participant.StrategyIntegrity
import com.github.tanokun.bakajinrou.plugin.method.AsBukkitItem
import com.github.tanokun.bakajinrou.plugin.method.itemKey
import org.bukkit.Bukkit
import org.bukkit.persistence.PersistentDataType
import java.util.*

class ParticipantStrategy(
    private val playerUniqueId: UUID,
    private val strategyIntegrity: StrategyIntegrity
): GrantedStrategy {
    private val methods = arrayListOf<GrantedMethod>()

    override fun grant(method: GrantedMethod) {
        strategyIntegrity.enableMethod(method)
        methods.add(method)

        if (method !is AsBukkitItem) return
        Bukkit.getPlayer(playerUniqueId)?.inventory?.addItem(method.createBukkitItem())
    }

    override fun remove(method: GrantedMethod) {
        strategyIntegrity.disableMethod(method)
        methods.removeAll { it.uniqueId == method.uniqueId }

        if (method !is AsBukkitItem) return
        Bukkit.getPlayer(playerUniqueId)?.inventory
            ?.filterNotNull()
            ?.filter {
                it.persistentDataContainer.getOrDefault(itemKey, PersistentDataType.STRING, "") == method.uniqueId.toString()
            }
            ?.forEach {
                it.amount = 0
            }
    }

    override fun getMethod(uniqueId: UUID): GrantedMethod? = methods.firstOrNull { it.uniqueId == uniqueId }

    override fun getActiveProtectiveMethods(holder: Participant): List<ProtectiveMethod> =
        methods
            .filterIsInstance<ProtectiveMethod>()
            .filter { it.isActive(holder) }
            .sortedBy { it.priority }
}