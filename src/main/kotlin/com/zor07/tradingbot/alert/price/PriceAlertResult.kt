package com.zor07.tradingbot.alert.price

data class PriceAlertResult(
    val changePercent: Double,
    val direction: Direction
) {
    enum class Direction { UP, DOWN }
}
