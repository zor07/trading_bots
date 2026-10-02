package com.zor07.tradingbot.exchange.binance.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BinanceLiquidationDto(
    @JsonProperty("o") val order: BinanceLiquidationOrderDto
)

data class BinanceLiquidationOrderDto(
    @JsonProperty("s") val symbol: String,
    @JsonProperty("S") val side: String,       // BUY or SELL
    @JsonProperty("p") val price: String,
    @JsonProperty("q") val quantity: String,
    @JsonProperty("T") val timestamp: Long
)
