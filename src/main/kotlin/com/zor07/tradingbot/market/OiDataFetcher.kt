package com.zor07.tradingbot.market

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.exchange.oi.OpenInterestClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OiDataFetcher(
    private val oiClients: List<OpenInterestClient>,
    private val settingsService: AlertSettingsService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun fetch(symbol: String): OiData? {
        val settings = settingsService.getOiSettings()
        val values = oiClients.mapNotNull { client ->
            runCatching { client.getOpenInterestHistory(symbol, settings.period) }
                .onFailure { log.warn("Failed to fetch OI for {} from {}: {}", symbol, client.exchangeName, it.message) }
                .getOrNull()
                ?.lastOrNull()
        }
        if (values.isEmpty()) return null
        return OiData(value = values.average())
    }
}
