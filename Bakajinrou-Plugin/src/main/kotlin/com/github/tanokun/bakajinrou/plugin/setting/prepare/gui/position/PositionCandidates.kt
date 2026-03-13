package com.github.tanokun.bakajinrou.plugin.setting.prepare.gui.position

import com.github.tanokun.bakajinrou.plugin.common.setting.RequestedPositions
import com.github.tanokun.bakajinrou.plugin.setting.prepare.desided.SelectedPositions

class PositionCandidates {
    private val selectedPositions =
        hashMapOf(
            RequestedPositions.WOLF to 3,
            RequestedPositions.MADMAN to 1,
            RequestedPositions.IDIOT to 3,
            RequestedPositions.FORTUNE to 1,
            RequestedPositions.MEDIUM to 1,
            RequestedPositions.KNIGHT to 1,
            RequestedPositions.FOX to 1
        )

    fun getAmountBy(positions: RequestedPositions): Int {
        return selectedPositions[positions] ?: 0
    }

    fun updateAmount(positions: RequestedPositions, amount: Int) {
        if (amount < 0) throw IllegalArgumentException("予約役職を、0未満にはできません。")

        selectedPositions[positions] = amount
    }

    fun increase(positions: RequestedPositions) { updateAmount(positions, getAmountBy(positions) + 1) }

    fun decrease(positions: RequestedPositions) { updateAmount(positions, getAmountBy(positions) - 1) }

    fun toSelected() = SelectedPositions(selectedPositions)
}