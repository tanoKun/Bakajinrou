package com.github.tanokun.bakajinrou.plugin.localization

import com.github.tanokun.bakajinrou.api.translate.TranslationKey

object UITranslationKeys {
    object Formatter {
        object Participant {
            val CITIZEN = TranslationKey("formatter.participant.citizens.citizen")
            val IDIOT = TranslationKey("formatter.participant.idiot")

            object Idiot {
                val FORTUNE = TranslationKey("formatter.participant.citizens.idiot.fortune")
                val MEDIUM = TranslationKey("formatter.participant.citizens.idiot.medium")
                val KNIGHT = TranslationKey("formatter.participant.citizens.idiot.knight")
            }

            object Mystic {
                val FORTUNE = TranslationKey("formatter.participant.citizens.mystic.fortune")
                val MEDIUM = TranslationKey("formatter.participant.citizens.mystic.medium")
                val KNIGHT = TranslationKey("formatter.participant.citizens.mystic.knight")
            }

            val WOLF = TranslationKey("formatter.participant.wolf")
            val MADMAN = TranslationKey("formatter.participant.madman")

            val FOX = TranslationKey("formatter.participant.fox")

            val SPECTATOR: TranslationKey = TranslationKey("formatter.participant.spectator")

            object State {
                val SUSPENDED: TranslationKey = TranslationKey("formatter.participant.state.suspended")
            }
        }

        object Category {
            val WOLF = TranslationKey("formatter.category.wolf")
            val MADMAN = TranslationKey("formatter.category.madman")
            val FORTUNE = TranslationKey("formatter.category.fortune")
            val MEDIUM = TranslationKey("formatter.category.medium")
            val KNIGHT = TranslationKey("formatter.category.knight")
            val CITIZEN = TranslationKey("formatter.category.citizen")
            val FOX = TranslationKey("formatter.category.fox")
            val SPECTATOR = TranslationKey("formatter.category.spectator")
        }
    }
}