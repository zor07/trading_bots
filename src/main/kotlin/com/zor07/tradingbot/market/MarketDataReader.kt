package com.zor07.tradingbot.market

import org.springframework.stereotype.Component

@Component
class MarketDataReader(private val cache: MarketDataCache) {

    fun getState(symbol: String): SymbolState? = cache.getState(symbol)

    fun getAll(): Map<String, SymbolState> = cache.getAll()
}
