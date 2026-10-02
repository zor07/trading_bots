package com.zor07.tradingbot.market

import org.springframework.stereotype.Component

@Component
class MarketDataWriter(private val cache: MarketDataCache) {

    fun setState(state: SymbolState) {
        cache.setState(state)
    }
}
