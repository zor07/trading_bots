package com.zor07.tradingbot.exchange.lsr

data class LongShortRatioSnapshot(
    val symbol: String,
    val accountLongRatio: Double,
    val positionLongRatio: Double
)
