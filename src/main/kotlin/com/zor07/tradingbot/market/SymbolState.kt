package com.zor07.tradingbot.market

data class SymbolState(
    val symbol: String,
    val price: PriceData? = null,
    val oi: OiData? = null
)
