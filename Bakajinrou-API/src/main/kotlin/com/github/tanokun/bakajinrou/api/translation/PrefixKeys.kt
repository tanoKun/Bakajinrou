package com.github.tanokun.bakajinrou.api.translation

open class PrefixKeys private constructor(key: String): TranslationKey("prefix.$key") {
    class Idiot private constructor(key: String) : PrefixKeys("idiot.$key") { companion object {
        val FORTUNE = Idiot("fortune")
        val MEDIUM = Idiot("medium")
        val KNIGHT = Idiot("knight")
    } }

    class Mystic private constructor(key: String) : PrefixKeys("mystic.$key") { companion object {
        val FORTUNE = Mystic("fortune")
        val MEDIUM = Mystic("medium")
        val KNIGHT = Mystic("knight")
    } }

    class ComingOut private constructor(key: String) : PrefixKeys("coming.out.$key") { companion object {
        val LAST_WOLF = ComingOut("last.wolf")
        val FORTUNE = ComingOut("fortune")
        val MEDIUM = ComingOut("medium")
        val KNIGHT = ComingOut("knight")
    } }

    companion object {
        val WOLF = PrefixKeys("wolf")
        val MADMAN = PrefixKeys("madman")
        val FOX = PrefixKeys("fox")
        val SPECTATOR = PrefixKeys("spectator")

        val IDIOT = PrefixKeys("idiot")
        val CITIZEN = PrefixKeys("citizen")
    }
}