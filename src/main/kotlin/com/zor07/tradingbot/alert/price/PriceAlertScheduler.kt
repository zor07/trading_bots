package com.zor07.tradingbot.alert.price

import com.zor07.tradingbot.config.properties.PriceAlertProperties
import com.zor07.tradingbot.exchange.ExchangeClient
import com.zor07.tradingbot.exchange.SymbolService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

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
        log.debug("Price alert tick: checking {} symbols across {} exchanges",
            symbols.size, exchangeClients.size)

        for (symbol in symbols) {
            val klines = exchangeClients
                .flatMap { client ->
                    client.getKlines(symbol, properties.candleInterval, properties.candleLimit)
                }

            val result = detector.detect(klines, properties.threshold) ?: continue
            alertService.handle(symbol, result.changePercent)
        }
    }
}
