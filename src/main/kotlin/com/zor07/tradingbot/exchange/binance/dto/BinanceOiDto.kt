package com.zor07.tradingbot.exchange.binance.dto

data class BinanceOiDto(
    val symbol: String,
    val sumOpenInterestValue: String,
    val timestamp: Long
)
