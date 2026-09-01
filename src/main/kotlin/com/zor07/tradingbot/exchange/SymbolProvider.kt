package com.zor07.tradingbot.exchange

interface SymbolProvider {
    fun getTopSymbolsByVolume(limit: Int): List<String>
}
