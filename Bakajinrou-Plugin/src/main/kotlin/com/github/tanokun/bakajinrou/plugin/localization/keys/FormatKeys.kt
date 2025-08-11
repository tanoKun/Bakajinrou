package com.github.tanokun.bakajinrou.plugin.localization.keys

import com.github.tanokun.bakajinrou.api.translate.TranslationKey

sealed class FormatKeys(key: String): TranslationKey("format.$key") {
    open class Participant private constructor(key: String): FormatKeys("participant.$key") {
        companion object {
            val CITIZEN = Participant("citizen")
            val WOLF = Participant("wolf")
            val MADMAN = Participant("madman")
            val FOX = Participant("fox")
        }

        class Idiot private constructor(key: String): Participant("idiot.$key") { companion object {
            val FORTUNE = Idiot("fortune")
            val MEDIUM = Idiot("medium")
            val KNIGHT = Idiot("knight")
        } }

        class Mystic private constructor(key: String): Participant("mystic.$key") { companion object {
            val FORTUNE = Mystic("fortune")
            val MEDIUM = Mystic("medium")
            val KNIGHT = Mystic("knight")
        } }

        class State private constructor(key: String): Participant("state.$key") { companion object {
            val SUSPENDED = State("suspended")
        } }
    }

    class Category private constructor(key: String): FormatKeys("category.$key") { companion object {
        val WOLF = Category("wolf")
        val MADMAN = Category("madman")
        val FORTUNE = Category("fortune")
        val MEDIUM = Category("medium")
        val KNIGHT = Category("knight")
        val CITIZEN = Category("citizen")
        val FOX = Category("fox")
    } }
}