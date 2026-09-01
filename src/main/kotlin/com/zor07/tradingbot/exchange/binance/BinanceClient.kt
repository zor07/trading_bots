package com.zor07.tradingbot.exchange.binance

import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.exchange.SymbolProvider
import com.zor07.tradingbot.exchange.model.Kline
import org.springframework.stereotype.Component

@Component
class BinanceClient : ExchangeClient, SymbolProvider {

    override val exchangeName: String = "BINANCE"

    override fun getKlines(symbol: String, candleInterval: String, limit: Int): List<Kline> {
        // TODO: implement in iteration 3
        return emptyList()
    }

    override fun getTopSymbolsByVolume(limit: Int): List<String> {
        // TODO: implement in iteration 2
        return emptyList()
    }
}
