package com.zor07.tradingbot.exchange

import com.zor07.tradingbot.config.properties.SymbolsProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SymbolCacheService(
    private val symbolProvider: SymbolProvider,
    private val properties: SymbolsProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Volatile
    private var cache: List<SymbolInfo> = emptyList()

    @Scheduled(fixedRateString = "\${alerts.symbols.update-interval}", initialDelay = 0)
    fun refresh() {
        log.info("Refreshing symbol cache...")
        cache = symbolProvider.getTopSymbolsByVolume(Int.MAX_VALUE).map { SymbolInfo(it) }
        log.info("Symbol cache refreshed: {} symbols", cache.size)
    }

    fun getAll(): List<SymbolInfo> = cache

    fun getSymbols(): List<String> = cache.map { it.symbol }

    fun getTopSymbols(n: Int): List<String> = getSymbols().take(n)
}
