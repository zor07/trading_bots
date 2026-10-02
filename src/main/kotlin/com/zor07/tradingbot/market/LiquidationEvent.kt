package com.zor07.tradingbot.market

data class LiquidationEvent(
    val symbol: String,
    val data: LiquidationData
)
