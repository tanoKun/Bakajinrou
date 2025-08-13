package com.github.tanokun.bakajinrou.plugin.localization

import com.github.tanokun.bakajinrou.api.translation.TranslationKey
import com.github.tanokun.bakajinrou.plugin.common.formatter.ColorPallet
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.translation.Translator
import org.bukkit.NamespacedKey
import java.text.MessageFormat
import java.util.*

class JinrouTranslator(
    private val dictionaries: Map<Locale, Dictionary>,
    colorPallet: ColorPallet
): Translator {
    private val colorTagResolver = colorPallet.colors.map { (key, color) ->
        Placeholder.styling(key, TextColor.color(color))
    }

    override fun translate(component: TranslatableComponent, locale: Locale): Component {
        val formatString = getMiniMessageFormat(component.key(), locale) ?: return component
        val placeholders = component.arguments().mapIndexed { index, arg ->
            Placeholder.component("arg-$index", arg)
        }

        return MiniMessage.miniMessage().deserialize(formatString,
            TagResolver.builder()
                .resolvers(colorTagResolver)
                .resolvers(placeholders)
                .build()
        )
    }

    fun translate(translationKey: TranslationKey, locale: Locale) = translate(Component.translatable(translationKey.key), locale)

    fun translate(translationKey: TranslationKey, locale: Locale, vararg arguments: ComponentLike) =
        translate(Component.translatable(translationKey.key).arguments(*arguments), locale)


    private fun getMiniMessageFormat(key: String, locale: Locale): String? {
        val dictionary = dictionaries[locale] ?: dictionaries[Locale.JAPAN] ?: return null

        return dictionary.vocabularies[key]
    }

    override fun name(): Key = NamespacedKey("jinrou", "translator")

    override fun translate(key: String, locale: Locale): MessageFormat? {
        return null
    }
}