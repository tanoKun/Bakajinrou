package com.github.tanokun.bakajinrou.game.crafting

enum class CraftingProduct(
    val availableInRandomCrafting: Boolean = true,
) {
    SWORD,
    GAS,
    RESISTANCE,
    SHIELD,
    SPEED,
    INVISIBILITY,
    EXCHANGE,
    SCATTER_CROSSBOW(availableInRandomCrafting = false),
}
