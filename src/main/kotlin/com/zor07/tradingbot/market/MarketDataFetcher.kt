package com.zor07.tradingbot.market

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MarketDataFetcher(
    private val priceDataFetcher: PriceDataFetcher,
    private val oiDataFetcher: OiDataFetcher
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun fetch(symbol: String): SymbolState {
        val price = priceDataFetcher.fetch(symbol)
        val oi = oiDataFetcher.fetch(symbol)
        log.info("{}: price={}, oi={}", symbol, price, oi)
        return SymbolState(symbol = symbol, price = price, oi = oi)
    }
}
