package com.zor07.tradingbot.market

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class MarketDataService(
    private val settingsService: AlertSettingsService,
    private val fetcher: MarketDataFetcher,
    private val detector: MarketDataDetector,
    private val writer: MarketDataWriter,
    private val publisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun refresh() {
        val symbols = settingsService.getWatchlist()
        log.debug("Market data refresh: {} symbols", symbols.size)

        runBlocking {
            symbols.map { symbol ->
                async(Dispatchers.IO) {
                    val curr = fetcher.fetch(symbol)
                    val delta = detector.detect(curr)
                    writer.setState(curr)
                    if (delta != null) {
                        log.debug("{}: publishing delta", symbol)
                        publisher.publishEvent(SymbolDeltaEvent(delta))
                    } else {
                        log.debug("{}: no delta, skipping event", symbol)
                    }
                }
            }.awaitAll()
        }
    }
}
