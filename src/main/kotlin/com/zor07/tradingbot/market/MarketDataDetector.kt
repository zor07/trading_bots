package com.zor07.tradingbot.market

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MarketDataDetector(private val reader: MarketDataReader) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun detect(curr: SymbolState): SymbolDelta? {
        val prev = reader.getState(curr.symbol)
        if (prev == null) {
            log.info("{}: no prev state, skipping delta", curr.symbol)
            return null
        }
        log.info("{}: prev={}, curr={}", curr.symbol, prev, curr)
        return SymbolDelta(symbol = curr.symbol, prev = prev, curr = curr)
    }
}
