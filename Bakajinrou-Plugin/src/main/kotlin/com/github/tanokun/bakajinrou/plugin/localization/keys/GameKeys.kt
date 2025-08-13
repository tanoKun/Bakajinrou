package com.github.tanokun.bakajinrou.plugin.localization.keys

import com.github.tanokun.bakajinrou.api.translation.TranslationKey

sealed class GameKeys(key: String): TranslationKey("game.$key") {
    class Start private constructor(key: String): GameKeys("start.$key") { companion object {
        val TITLE = Start("title")
        val SUB_TITLE = Start("sub.title")
    } }

    open class Finish private constructor(key: String): GameKeys("finish.$key") {
        companion object {
            val VICTORY_MESSAGE = Finish("victory.message")
            val LOSE_MESSAGE = Finish("lose.message")
        }

        class Citizen private constructor(key: String): Finish("citizen.$key") { companion object {
            val TITLE = Citizen("title")
            val MESSAGE = Citizen("message")
        } }

        class Wolf private constructor(key: String): Finish("wolf.$key") { companion object {
            val TITLE = Wolf("title")
            val MESSAGE = Wolf("message")
        } }

        class Fox private constructor(key: String): Finish("fox.$key") { companion object {
            val TITLE = Fox("title")
            val MESSAGE = Fox("message")
        } }
    }

    open class Gui private constructor(key: String): GameKeys("gui.$key") {
        companion object {
            val TITLE = Gui("title")
        }

        class Using private constructor(key: String): Gui("using.$key") { companion object {
            val DIVINE_DESCRIPTION = Using("divine.description")
            val COMMUNE_DESCRIPTION = Using("commune.description")
            val PROTECT_DESCRIPTION = Using("protect.description")
        } }
    }


    open class Ability private constructor(key: String): GameKeys("ability.$key") {
        class Using private constructor(key: String): Ability("using.$key") { companion object {
            val DIVINE_MESSAGE = Using("divine.message")
            val COMMUNE_MESSAGE = Using("commune.message")
            val COMMUNE_FAILURE_MESSAGE = Using("commune.failure.message")
            val PROTECT_MESSAGE = Using("protect.message")
        } }
    }

    class Announcement private constructor(key: String): GameKeys("announcement.$key") { companion object {
        val REMAINING_TIME = Announcement("remaining.time")
        val GLOWING = Announcement("glowing")
        val GETAWAY = Announcement("getaway")
        val REVELATION = Announcement("revelation")
    } }
}