package com.zor07.tradingbot.exchange.binance.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BinanceTickerDto(
    val symbol: String,
    val quoteVolume: String
)
