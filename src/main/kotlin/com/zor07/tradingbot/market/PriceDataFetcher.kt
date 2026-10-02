package com.zor07.tradingbot.market

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.exchange.ExchangeClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PriceDataFetcher(
    private val exchangeClients: List<ExchangeClient>,
    private val settingsService: AlertSettingsService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun fetch(symbol: String): PriceData? {
        val settings = settingsService.getPriceSettings()
        val klines = exchangeClients.flatMap { client ->
            runCatching { client.getKlines(symbol, settings.candleInterval, settings.candleLimit) }
                .onFailure { log.warn("Failed to fetch klines for {} from {}: {}", symbol, client.exchangeName, it.message) }
                .getOrElse { emptyList() }
        }
        if (klines.isEmpty()) return null
        return PriceData(
            open = BigDecimal.valueOf(klines.map { it.open.toDouble() }.average()),
            close = BigDecimal.valueOf(klines.map { it.close.toDouble() }.average())
        )
    }
}
