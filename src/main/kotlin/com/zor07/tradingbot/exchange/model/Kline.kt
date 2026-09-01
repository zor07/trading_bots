package com.zor07.tradingbot.exchange.model

import java.math.BigDecimal
import java.time.Instant

data class Kline(
    val symbol: String,
    val exchange: String,
    val openTime: Instant,
    val closeTime: Instant,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal
)
