package com.zor07.tradingbot.exchange.oi

interface OpenInterestClient {
    val exchangeName: String
    fun getOpenInterestHistory(symbol: String, period: String): List<Double> // USD values, oldest first
}
