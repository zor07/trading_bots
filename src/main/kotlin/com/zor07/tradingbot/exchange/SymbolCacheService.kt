package com.zor07.tradingbot.exchange

import com.zor07.tradingbot.config.properties.SymbolsProperties
import com.zor07.tradingbot.exchange.lsr.LongShortRatioClient
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SymbolCacheService(
    private val symbolProvider: SymbolProvider,
    private val lsrClients: List<LongShortRatioClient>,
    private val properties: SymbolsProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Volatile
    private var cache: List<SymbolInfo> = emptyList()

    @PostConstruct
    fun init() = refresh()

    @Scheduled(fixedRateString = "\${alerts.symbols.update-interval}")
    fun refresh() {
        log.info("Refreshing symbol cache...")
        val allSymbols = symbolProvider.getTopSymbolsByVolume(Int.MAX_VALUE)
        log.info("Loaded {} symbols, checking LSR support...", allSymbols.size)

        cache = allSymbols.map { symbol ->
            val hasLsr = lsrClients.any { client ->
                runCatching { client.getLsrByAccounts(symbol) != null }
                    .getOrDefault(false)
            }
            SymbolInfo(symbol = symbol, hasLsr = hasLsr)
        }

        val lsrCount = cache.count { it.hasLsr }
        log.info("Symbol cache refreshed: {} total, {} with LSR", cache.size, lsrCount)
    }

    fun getAll(): List<SymbolInfo> = cache

    fun getSymbols(): List<String> = cache.map { it.symbol }

    fun getTopSymbols(n: Int): List<String> = getSymbols().take(n)
}
