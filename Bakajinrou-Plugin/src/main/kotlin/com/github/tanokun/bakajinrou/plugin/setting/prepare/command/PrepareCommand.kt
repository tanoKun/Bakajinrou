package com.github.tanokun.bakajinrou.plugin.setting.prepare.command

import com.github.tanokun.bakajinrou.plugin.common.coroutine.TopCoroutineScope
import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.common.setting.template.DistributionTemplates
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.map.GameMapRegistry
import com.github.tanokun.bakajinrou.plugin.presentation.sidebar.prepare.PreparedGameSidebarRenderer
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedMap
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedParticipants
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.map.SelectMapGui
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.participant.SelectParticipantGui
import com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.position.SelectPositionGui
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.annotation.Single
import plutoproject.adventurekt.component
import plutoproject.adventurekt.text.color
import plutoproject.adventurekt.text.deco
import plutoproject.adventurekt.text.style.*
import plutoproject.adventurekt.text.text

val MAP = NamespacedKey("jinrou", "prepared_game_map")
val PARTICIPANTS = NamespacedKey("jinrou", "prepared_game_participants")
val POSITIONS = NamespacedKey("jinrou", "prepared_game_positions")

@Single
class PrepareCommand(
    mapRegistry: GameMapRegistry,
    templates: DistributionTemplates,
    jinrouTranslator: JinrouTranslator,
    singleScope: TopCoroutineScope
) {
    init {
        CommandAPICommand("prepare").withPermission("bakajinrou.command.prepare")
            .executesPlayer(PlayerCommandExecutor { sender, _ -> singleScope.launch {
                var renderer = PreparedGameSidebarRenderer(
                    null, null,
                    SelectedPositions(
                        mapOf(
                            RequestedPositions.WOLF to 0,
                            RequestedPositions.MADMAN to 0,
                            RequestedPositions.IDIOT to 0,
                            RequestedPositions.FORTUNE to 0,
                            RequestedPositions.MEDIUM to 0,
                            RequestedPositions.KNIGHT to 0,
                            RequestedPositions.FOX to 0
                        )
                    ), jinrouTranslator
                )

                renderer.renderGameOverview(sender)

                coroutineContext.job.invokeOnCompletion {
                    renderer.deleteAll()
                }

                suspend fun <T> step(
                    selector: suspend () -> T?,
                    updater: (PreparedGameSidebarRenderer, T) -> PreparedGameSidebarRenderer
                ): T? {
                    val pickupSound = Sound.sound(NamespacedKey("minecraft", "entity.experience_orb.pickup"), Sound.Source.PLAYER, 1.0f, 1.0f)
                    val cancelMsg = component { text("ゲーム設定がキャンセルされました") color red }

                    val result = selector() ?: run {
                        sender.sendMessage(cancelMsg)
                        return null
                    }

                    sender.playSound(pickupSound)

                    renderer.deleteAll()
                    renderer = updater(renderer, result)
                    renderer.renderGameOverview(sender)

                    return result
                }

                val selectedMap = step({
                    SelectMapGui(mapRegistry.findAll(), null).deferredSelection(sender)
                }) { r, res ->
                    r.copy(selectedMap = res)
                } ?: return@launch

                val selectedParticipants = step({
                    SelectParticipantGui(Bukkit.getOnlinePlayers().toList()).deferredSelection(sender)
                }) { r, res ->
                    r.copy(selectedParticipants = res)
                } ?: return@launch

                val selectedPositions = step({
                    SelectPositionGui(templates, jinrouTranslator, selectedParticipants.participants.size).deferredSelection(sender)
                }) { r, res ->
                    r.copy(selectedPositions = res)
                } ?: return@launch

                sender.closeInventory()

                sender.inventory.addItem(createItem(selectedMap, selectedParticipants, selectedPositions))
            } })
            .register()
    }

    fun createItem(
        map: SelectedMap,
        participants: SelectedParticipants,
        positions: SelectedPositions
    ): ItemStack {
        val item = ItemStack(Material.FILLED_MAP)

        item.editMeta {
            it.displayName(
                component {
                    text("ゲーム設定") color blue deco bold
                }
            )

            it.lore(listOf(
                component {
                    text("「右クリック」") color white deco bold
                    text("で、ゲームを開始します。") color gray
                }
            ))
        }

        item.editMeta {
            val container = it.persistentDataContainer

            val jsonMap = Json.encodeToString(SelectedMap.serializer(), map)
            val jsonParticipants = Json.encodeToString(SelectedParticipants.serializer(), participants)
            val jsonPositions = Json.encodeToString(SelectedPositions.serializer(), positions)

            container.set(MAP, PersistentDataType.STRING, jsonMap)
            container.set(PARTICIPANTS, PersistentDataType.STRING, jsonParticipants)
            container.set(POSITIONS, PersistentDataType.STRING, jsonPositions)
        }

        return item
    }
}
