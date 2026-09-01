package com.zor07.tradingbot.exchange.bybit

import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.exchange.model.Kline
import org.springframework.stereotype.Component

@Component
class BybitClient : ExchangeClient {

    override val exchangeName: String = "BYBIT"

    override fun getKlines(symbol: String, candleInterval: String, limit: Int): List<Kline> {
        // TODO: implement in iteration 3
        return emptyList()
    }
}
