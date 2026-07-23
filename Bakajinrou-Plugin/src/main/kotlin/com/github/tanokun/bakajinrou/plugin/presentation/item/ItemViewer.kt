package com.github.tanokun.bakajinrou.plugin.presentation.item

import com.github.tanokun.bakajinrou.api.translation.MethodAssetKeys
import com.github.tanokun.bakajinrou.plugin.common.item.ItemPersistent
import com.github.tanokun.bakajinrou.plugin.localization.JinrouTranslator
import com.github.tanokun.bakajinrou.plugin.localization.keys.displayName
import com.github.tanokun.bakajinrou.plugin.localization.keys.lore
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

object ItemViewer {
    private val miniMessage = miniMessage()

    /**
     * 基本的な表示アイテムを作成します。
     *
     * @param material 表示上のマテリアル
     * @param isGlowing エンチャント状態にするか
     * @param assetKey 表示名と説明文の翻訳に使うアセットキー
     * @param locale 翻訳先
     * @param translator 人狼用の翻訳機
     */
    fun createBasicItem(
        material: Material,
        isGlowing: Boolean,
        assetKey: MethodAssetKeys,
        translator: JinrouTranslator,
        locale: Locale,
    ): ItemStack {
        val item = ItemStack.of(material).apply {
            editMeta {
                it.addItemFlags(*ItemFlag.entries.toTypedArray())
                if (isGlowing) it.addEnchant(Enchantment.UNBREAKING, 1, true)
            }

            translateBasic(assetKey, translator, locale)
        }

        return item
    }

    /**
     * アイテムの基本表示(名前・説明文)を翻訳して設定します。
     *
     * @receiver 設定対象の [ItemStack]
     * @param assetKey 翻訳対象のアセットキー
     * @param translator 翻訳処理を行う [JinrouTranslator]
     * @param locale 翻訳を行うロケール
     */
    fun ItemStack.translateBasic(assetKey: MethodAssetKeys, translator: JinrouTranslator, locale: Locale) {
        this.editMeta {
            val displayName = translator
                .translate(assetKey.displayName(), locale)
                .decoration(TextDecoration.ITALIC, false)
            it.displayName(displayName)

            val lore = splitLore(translator.translate(assetKey.lore(), locale))
            it.lore(lore)
        }
    }

    /**
     * ロア(説明文)を改行タグで分割します。
     *
     * @param lore 翻訳済みのロアコンポーネント
     *
     * @return 改行ごとに分割されたロアのリスト
     */
    private fun splitLore(lore: Component): List<Component> {
        val serialized = miniMessage.serialize(lore)

        return serialized.split("<newline>", "<br>").map {
            miniMessage
                .deserialize(it)
                .decoration(TextDecoration.ITALIC, false)
        }
    }


    fun ItemStack.hasPossibilityOfMethod(): Boolean = this.persistentDataContainer.has(ItemPersistent.JINROU_ITEM_MAKER)

    fun ItemStack.isVisible(): Boolean = this.persistentDataContainer.getOrDefault(ItemPersistent.IS_VISIBLE, PersistentDataType.BOOLEAN, true)

    fun ItemStack.isTransportable(): Boolean = this.persistentDataContainer.getOrDefault(ItemPersistent.IS_TRANSPORTABLE, PersistentDataType.BOOLEAN, true)
}
