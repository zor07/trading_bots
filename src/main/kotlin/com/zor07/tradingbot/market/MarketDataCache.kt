package com.zor07.tradingbot.market

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class MarketDataCache {

    private val data = ConcurrentHashMap<String, SymbolState>()

    fun getState(symbol: String): SymbolState? = data[symbol]

    fun getAll(): Map<String, SymbolState> = data.toMap()

    fun setState(state: SymbolState) {
        data[state.symbol] = state
    }
}
