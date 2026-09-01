package com.zor07.tradingbot.exchange

import com.zor07.tradingbot.config.properties.SymbolsProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class SymbolService(
    private val symbolProvider: SymbolProvider,
    private val properties: SymbolsProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Volatile
    private var symbols: List<String> = emptyList()

    @PostConstruct
    fun init() {
        refresh()
    }

    @Scheduled(fixedDelayString = "#{@symbolsProperties.updateInterval.toMillis()}")
    fun refresh() {
        log.info("Refreshing top {} symbols by volume", properties.topN)
        symbols = symbolProvider.getTopSymbolsByVolume(properties.topN)
        log.info("Loaded symbols: {}", symbols)
    }

    fun getSymbols(): List<String> = symbols
}
