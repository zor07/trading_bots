package com.zor07.tradingbot.exchange.lsr

interface LongShortRatioClient {
    val exchangeName: String
    fun getLsrByAccounts(symbol: String): Double?   // long ratio 0..100
    fun getLsrByPositions(symbol: String): Double?  // long ratio 0..100
}
