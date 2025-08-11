package com.github.tanokun.bakajinrou.api.translate

sealed class MethodAssetKeys(key: String): TranslationKey("method.$key") {
    class Attack private constructor(key: String) : MethodAssetKeys("attack.$key") { companion object {
        val DAMAGE_POTION = Attack("damage.potion")
        val SWORD = Attack("sword")
        val ARROW = Attack("arrow")
    }}

    class Protective private constructor(key: String) : MethodAssetKeys("protective.$key") { companion object {
        val TOTEM = Protective("totem")
        val SHIELD = Protective("shield")
        val RESISTANCE = Protective("resistance")
        val FAKE_TOTEM = Protective("fake.totem")
    }}

    class Advantage private constructor(key: String): MethodAssetKeys("advantage.$key") {companion object {
        val EXCHANGE = Advantage("exchange")
        val SPEED = Advantage("speed")
        val INVISIBILITY = Advantage("invisibility")
    }}

    class Ability private constructor(key: String) : MethodAssetKeys("ability.$key") { companion object {
        val DIVINE = Ability("divine")
        val COMMUNE = Ability("commune")
        val PROTECT = Ability("protect")
    }}
}