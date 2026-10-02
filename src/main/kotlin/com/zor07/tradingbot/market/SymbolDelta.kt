package com.zor07.tradingbot.market

data class SymbolDelta(
    val symbol: String,
    val prev: SymbolState,
    val curr: SymbolState
)
