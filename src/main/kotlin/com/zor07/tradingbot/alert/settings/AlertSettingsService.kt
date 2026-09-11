package com.zor07.tradingbot.alert.settings

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zor07.tradingbot.alert.model.AlertType
import com.zor07.tradingbot.config.properties.LongShortRatioProperties
import com.zor07.tradingbot.config.properties.PriceAlertProperties
import com.zor07.tradingbot.config.properties.SymbolsProperties
import com.zor07.tradingbot.exchange.SymbolCacheService
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AlertSettingsService(
    private val repository: AlertSettingsRepository,
    private val objectMapper: ObjectMapper,
    private val priceAlertProperties: PriceAlertProperties,
    private val lsrProperties: LongShortRatioProperties,
    private val symbolsProperties: SymbolsProperties,
    private val symbolCacheService: SymbolCacheService
) {

    fun getPriceSettings(): PriceAlertSettings {
        val entity = repository.findByAlertType(AlertType.PRICE_MOVE.name)
            ?: return PriceAlertSettings(
                threshold = priceAlertProperties.threshold,
                candleInterval = priceAlertProperties.candleInterval,
                candleLimit = priceAlertProperties.candleLimit
            )
        return objectMapper.readValue(entity.settings)
    }

    fun savePriceSettings(settings: PriceAlertSettings) {
        save(AlertType.PRICE_MOVE.name, settings)
    }

    fun getLsrSettings(): LongShortRatioSettings {
        val entity = repository.findByAlertType(AlertType.LONG_SHORT_RATIO.name)
            ?: return LongShortRatioSettings(
                accountThreshold = lsrProperties.accountThreshold,
                positionThreshold = lsrProperties.positionThreshold
            )
        return objectMapper.readValue(entity.settings)
    }

    fun saveLsrSettings(settings: LongShortRatioSettings) {
        save(AlertType.LONG_SHORT_RATIO.name, settings)
    }

    fun getWatchlist(): List<String> {
        val entity = repository.findByAlertType(AlertType.SYMBOL_WATCHLIST.name)
            ?: return symbolCacheService.getTopSymbols(symbolsProperties.limit)
        return objectMapper.readValue<SymbolWatchlistSettings>(entity.settings).symbols
    }

    fun saveWatchlist(symbols: List<String>) {
        save(AlertType.SYMBOL_WATCHLIST.name, SymbolWatchlistSettings(symbols))
    }

    private fun save(alertType: String, settings: Any) {
        val json = objectMapper.writeValueAsString(settings)
        val existing = repository.findByAlertType(alertType)
        if (existing != null) {
            repository.save(existing.copy(settings = json, updatedAt = Instant.now()))
        } else {
            repository.save(AlertSettings(alertType = alertType, settings = json))
        }
    }
}
