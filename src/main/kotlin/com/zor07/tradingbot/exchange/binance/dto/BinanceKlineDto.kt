package com.zor07.tradingbot.exchange.binance.dto

import com.fasterxml.jackson.databind.JsonNode
import com.zor07.tradingbot.exchange.model.Kline
import java.math.BigDecimal
import java.time.Instant

// Binance returns: [openTime, open, high, low, close, volume, closeTime, ...]
fun JsonNode.toKline(symbol: String, exchange: String): Kline = Kline(
    symbol = symbol,
    exchange = exchange,
    openTime = Instant.ofEpochMilli(this[0].asLong()),
    open = BigDecimal(this[1].asText()),
    high = BigDecimal(this[2].asText()),
    low = BigDecimal(this[3].asText()),
    close = BigDecimal(this[4].asText()),
    volume = BigDecimal(this[5].asText()),
    closeTime = Instant.ofEpochMilli(this[6].asLong())
)
