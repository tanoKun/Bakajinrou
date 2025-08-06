package com.github.tanokun.bakajinrou.api.translate

object TranslationKeys {
    object Method {
        object Protective {
            val TOTEM = TranslationKey("method.protective.fake.totem")
            val SHIELD = TranslationKey("method.protective.fake.shield")
            val RESISTANCE = TranslationKey("method.protective.resistance")
            val FAKE_TOTEM = TranslationKey("method.protective.fake.totem")
        }

        object Attack {
            val DAMAGE_POTION = TranslationKey("method.attack.damage.potion")
            val SWORD = TranslationKey("method.attack.sword")
            val ARROW = TranslationKey("method.attack.arrow")
        }

        object Ability {
            val FORTUNE = TranslationKey("method.ability.fortune")
            val MEDIUM = TranslationKey("method.ability.medium")
        }
    }

    object Ability {
        object Result {
            val WOLF = TranslationKey("ability.result.wolf")
            val FOX = TranslationKey("ability.result.fox")
            val CITIZENS = TranslationKey("ability.result.citizens")
        }
    }

    object Prefix {
        object Citizens {
            val CITIZEN = TranslationKey("prefix.citizens.citizen")

            object Idiot {
                val FORTUNE = TranslationKey("prefix.citizens.idiot.fortune")
                val MEDIUM = TranslationKey("prefix.citizens.idiot.medium")
                val KNIGHT = TranslationKey("prefix.citizens.idiot.knight")
            }

            object Mystic {
                val FORTUNE = TranslationKey("prefix.citizens.mystic.fortune")
                val MEDIUM = TranslationKey("prefix.citizens.mystic.medium")
                val KNIGHT = TranslationKey("prefix.citizens.mystic.knight")
            }
        }

        object ComingOut {
            val LAST_WOLF = TranslationKey("prefix.coming.out.last.wolf")
            val FORTUNE = TranslationKey("prefix.coming.out.fortune")
            val MEDIUM = TranslationKey("prefix.coming.out.medium")
            val KNIGHT = TranslationKey("prefix.coming.out.knight")
        }

        val WOLF = TranslationKey("prefix.wolf")
        val MADMAN = TranslationKey("prefix.madman")

        val FOX = TranslationKey("prefix.fox")

        val SPECTATOR: TranslationKey = TranslationKey("prefix.spectator")
    }
}