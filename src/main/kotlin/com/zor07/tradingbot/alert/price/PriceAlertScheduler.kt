package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.config.properties.PriceAlertProperties
import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.exchange.SymbolService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class PriceAlertScheduler(
    private val exchangeClients: List<ExchangeClient>,
    private val symbolService: SymbolService,
    private val detector: PriceAlertDetector,
    private val alertService: PriceAlertService,
    private val properties: PriceAlertProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${alerts.price.interval}")
    fun run() {
        val symbols = symbolService.getSymbols()
        log.info("Price alert tick: {} symbols, {} exchanges", symbols.size, exchangeClients.size)

        for (symbol in symbols) {
            val changes = exchangeClients.mapNotNull { client ->
                runCatching {
                    val klines = client.getKlines(symbol, properties.candleInterval, properties.candleLimit)
                    detector.computeChange(klines)
                }.onFailure {
                    log.warn("Failed to get klines for {} from {}: {}", symbol, client.exchangeName, it.message)
                }.getOrNull()
            }

            if (changes.isEmpty()) continue

            val avgChange = changes.average()
            log.info("{} avgChange={}%", symbol, String.format("%.2f", avgChange))

            if (abs(avgChange) >= properties.threshold) {
                alertService.handle(symbol, avgChange)
            }
        }
    }
}
