package com.zor07.tradingbot.exchange

import com.zor07.tradingbot.exchange.model.Kline

interface ExchangeClient {
    val exchangeName: String
    fun getKlines(symbol: String, candleInterval: String, limit: Int): List<Kline>
}
