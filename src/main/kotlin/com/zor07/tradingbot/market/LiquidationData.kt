package com.zor07.tradingbot.market

import java.time.Instant

data class LiquidationData(
    val side: String,       // BUY or SELL
    val price: Double,
    val quantity: Double,
    val usdValue: Double,
    val timestamp: Instant
)
